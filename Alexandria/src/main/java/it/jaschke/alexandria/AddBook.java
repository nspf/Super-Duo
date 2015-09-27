package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    private EditText ean;
    private final int LOADER_ID = 1;
    private View rootView;
    private final String EAN_CONTENT="eanContent";


    public static final String SCAN_FORMAT = "scanFormat";
    public static final String SCAN_CONTENTS = "scanContents";

    private String eanStr = "";

    private static final int BARCODE_SCANNER_REQUEST = 1;

    public AddBook(){
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(ean!=null) {
            outState.putString(EAN_CONTENT, ean.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        ean = (EditText) rootView.findViewById(R.id.ean);

        ean.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ean =s.toString();
                //catch isbn10 numbers
                if(ean.length()==10 && !ean.startsWith("978")){
                    ean="978"+ean;
                }


                if(ean.length()<13){
                    /**
                     * Hsiao-Lu says:
                     *
                     * “The app could use some work. Sometimes when I add a book
                     * and don’t double-check the ISBN, it just disappears!”
                     *
                     * Don't clear fields on text changed, and update the fields
                     * only when a valid book was fetched.
                     *
                     * Fields are cleared after pressing Next / Delete buttons
                     *
                     */
                     //clearFields();

                    return;
                }


                //Once we have an ISBN, start a book intent
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean);
                bookIntent.setAction(BookService.FETCH_BOOK);
                getActivity().startService(bookIntent);
                AddBook.this.restartLoader();

            }
        });

        rootView.findViewById(R.id.scan_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    // This is the callback method that the system will invoke when your button is
                    // clicked. You might do this by launching another app or by including the
                    //functionality directly in this app.
                    // Hint: Use a Try/Catch block to handle the Intent dispatch gracefully, if you
                    // are using an external app.
                    //when you're done, remove the toast below.

                    /**
                     * User Feedback handled
                     *
                     * Lauren says:
                     *
                     * “I like this app generally, and the speed at which books come up on my phone
                     *  after I enter the ISBN is awesome.
                     *  I’m frustrated that the scanning functionality isn’t implemented yet.
                     *  That would speed up the whole process and make the app way more useful for me.”
                     *
                     * Josh says:
                     *
                     * “This app is terrible. They say you can scan books, but that functionality
                     *  isn’t in the app yet...."
                     */

                    Intent intent = new Intent(getActivity(), ScanBookActivity.class);
                    startActivityForResult(intent, BARCODE_SCANNER_REQUEST);

                }
            });

        rootView.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ean.setText("");
                /**
                 * Hsiao-Lu says:
                 *
                 * “The app could use some work. Sometimes when I add a book
                 * and don’t double-check the ISBN, it just disappears!”
                 *
                 * Clear fields after pressing Next button.
                 *
                 */
                clearFields();

            }
        });

        rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**
                 * The delete button doesn't work when ean has a 10 digit book code.
                 * So, instead of passing the current ean value, now we send the code
                 * converted to 13 digit, stored previously in eanStr.
                 */

                Intent bookIntent = new Intent(getActivity(), BookService.class);
                //bookIntent.putExtra(BookService.EAN, ean.getText().toString());
                bookIntent.putExtra(BookService.EAN, eanStr);
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                ean.setText("");

                /**
                 *
                 * Clear fields after pressing Delete button.
                 *
                 */
                clearFields();
            }
        });

        if(savedInstanceState!=null){
            ean.setText(savedInstanceState.getString(EAN_CONTENT));
            ean.setHint("");
        }

        return rootView;
    }



    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(ean.getText().length()==0){
            return null;
        }


        //String eanStr= ean.getText().toString();

        /**
         * Defined eanStr at the beginning of the class,
         * to use it later to delete the book fetched, pressing the Cancel button.
         */
        eanStr= ean.getText().toString();

        if(eanStr.length()==10 && !eanStr.startsWith("978")){
            eanStr="978"+eanStr;
        }

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        ((TextView) rootView.findViewById(R.id.bookTitle)).setText(bookTitle);

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText(bookSubTitle);

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));

        /**
         * In some cases, the book fetched has no author data.
         * So we need to check if authors is not null.
         *
         */
        if(authors != null) {
            String[] authorsArr = authors.split(",");
            ((TextView) rootView.findViewById(R.id.authors)).setLines(authorsArr.length);
            ((TextView) rootView.findViewById(R.id.authors)).setText(authors.replace(",","\n"));
        }



        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if(Patterns.WEB_URL.matcher(imgUrl).matches()){
            //new DownloadImage((ImageView) rootView.findViewById(R.id.bookCover)).execute(imgUrl);
            //rootView.findViewById(R.id.bookCover).setVisibility(View.VISIBLE);

            /**
             * In some tests, the previous code for image loading makes the app crashing
             * with the following error:
             *
             * "MalformedURLException: Protocol not found"
             *
             * Picasso is a more efficient solution for loading images.
             *
             */
            ImageView bookCover = (ImageView) rootView.findViewById(R.id.bookCover);
            Picasso.with(getActivity())
                    .load(imgUrl)
                    .into(bookCover);
            bookCover.setVisibility(View.VISIBLE);


        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        ((TextView) rootView.findViewById(R.id.categories)).setText(categories);

        rootView.findViewById(R.id.save_button).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.delete_button).setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private void clearFields(){
        ((TextView) rootView.findViewById(R.id.bookTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.authors)).setText("");
        ((TextView) rootView.findViewById(R.id.categories)).setText("");
        rootView.findViewById(R.id.bookCover).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.save_button).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.delete_button).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
    }

    /**
     * Called after barcode scanner Activity is finished.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_SCANNER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle res = data.getExtras();
                ean.setText(res.getString(SCAN_CONTENTS));
            }
        }
    }
}
