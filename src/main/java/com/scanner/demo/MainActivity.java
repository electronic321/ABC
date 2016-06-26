package com.scanner.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.itextpdf.text.Image.getInstance;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private static final int REQUEST_CODE = 99;
    String path = null;

    private Button scanButton, cameraButton, mediaButton, pdf, openPDF;
    Toolbar toolbar;
    File[] listFile;
    GridView gridView;
    String outputPath, inputPath;
    int pos;
    public String FolderPath = Environment.getExternalStorageDirectory().getPath() + "/Scan Camera";
    final ArrayList<String> folder = new ArrayList<String>();// list of file paths
    File[] files;
    List<GridViewItem> gridItems;


    private void setGridAdapter(String path) {
        // Create a new grid adapter
        gridItems = createGridItems(path);
        MyGridAdapter adapter = new MyGridAdapter(this, gridItems);

        // Set the grid adapter
        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(adapter);

        // Set the onClickListener
        gridView.setOnItemClickListener(this);
    }

    private List<GridViewItem> createGridItems(String directoryPath) {
        List<GridViewItem> items = new ArrayList<GridViewItem>();

        // List all the items within the folder.
        files = new File(directoryPath).listFiles(new ImageFileFilter());

        if (files != null) {
            for (File file : files) {
                // Add the directories containing images or sub-directories
                if (file.isDirectory()
                        && file.listFiles(new ImageFileFilter()).length > 0) {
                    Log.e("File Paths are >", String.valueOf(file));

                    items.add(new GridViewItem(file.getAbsolutePath(), true, null));
                }
                // Add the images
                else {
                    Bitmap image = BitmapHelper.decodeBitmapFromFile(file.getAbsolutePath(),
                            50,
                            50);
                    items.add(new GridViewItem(file.getAbsolutePath(), false, image));
                }
            }
        }

        return items;
    }

    private boolean isImageFile(String filePath) {
        if (filePath.endsWith(".jpg") || filePath.endsWith(".png") || filePath.endsWith(".pdf")) {
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (gridItems.get(position).isDirectory()) {
            setGridAdapter(gridItems.get(position).getPath());


        }
        else {
            Intent intent = new Intent(MainActivity.this, ImageOpenedActivity.class);
            intent.putExtra("Image", gridItems.get(position).getPath());
            startActivity(intent);
        }

    }

    private class ImageFileFilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }
            else if (isImageFile(file.getAbsolutePath())) {
                Log.e("In Main Activity:::", file.getAbsolutePath());

                return true;
            }
            return false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
        setGridAdapter(FolderPath); // or variable named FolderPath

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked())
                    menuItem.setChecked(false);
                else
                    menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.shareIt:
                        Toast.makeText(getApplicationContext(), "Share It", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.rateUs:
                        Toast.makeText(getApplicationContext(), "Rate Us", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.aboutUs:
                        Toast.makeText(getApplicationContext(), "About Us", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Please Try Again", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();


    }

    private void init() {
//        pdf = (Button)findViewById(R.id.PDF);
//        openPDF = (Button)findViewById(R.id.openPDF);
        scanButton = (Button) findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new ScanButtonClickListener());
        cameraButton = (Button) findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new ScanButtonClickListener(ScanConstants.OPEN_CAMERA));
        mediaButton = (Button) findViewById(R.id.mediaButton);
        mediaButton.setOnClickListener(new ScanButtonClickListener(ScanConstants.OPEN_MEDIA));
    }

    public static boolean convertToPdf(String jpgFilePath, String outputPdfPath) {
        try {
            // Check if Jpg file exists or not
            File inputFile = new File(jpgFilePath);
            if (!inputFile.exists())
                throw new Exception("File '" + jpgFilePath + "' doesn't exist.");

            // Create output file if needed
            File outputFile = new File(outputPdfPath);
            if (!outputFile.exists())
                outputFile.createNewFile();

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            document.open();

            com.itextpdf.text.Image image = getInstance(jpgFilePath);
            image.setAbsolutePosition(0, 0);
            image.scaleAbsolute((float) 2549 * (float) .24, (float) 3304 * (float) .24);
            image.scaleToFit((float) 2549 * (float) .24, (float) 3304 * (float) .24);
            document.add(image);
            document.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private class ScanButtonClickListener implements View.OnClickListener {

        private int preference;

        public ScanButtonClickListener(int preference) {
            this.preference = preference;

        }

        public ScanButtonClickListener() {
        }

        @Override
        public void onClick(View v) {
            startScan(preference);
        }
    }

    public void startScan(int preference) {
        Log.e("Folder Path is : ", ScanConstants.IMAGE_PATH);
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);
                Uri tempUri = getImageUri(getApplicationContext(), bitmap);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(getRealPathFromURI(tempUri));
                path = String.valueOf(finalFile);
                Log.e("Path is ::::", String.valueOf(finalFile));
                new SingleMediaScanner(getApplicationContext(), finalFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private Bitmap convertByteArrayToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }


    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_main, menu);

        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
            case R.id.gallery:
                startScan(ScanConstants.PICKFILE_FROM_GALLERY);
//                new ScanButtonClickListener(ScanConstants.OPEN_MEDIA);
                // return true;

            case R.id.email:
                if (outputPath != null) {
                    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                    emailIntent.setType("application/image");
                    emailIntent.setType("message/rfc822");
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"example@example.com"});
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Test Subject");
                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "From SCAN CAMERA");
                    File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "out.pdf");
                    File file = new File(outputPath);
                    Uri path = Uri.fromFile(file);
                    emailIntent.putExtra(Intent.EXTRA_STREAM, path);
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                    Toast.makeText(getApplicationContext(), "Clicked Email " + String.valueOf(id), Toast.LENGTH_SHORT).show();
                }
                else
                    startScan(ScanConstants.PICKFILE_FROM_GALLERY);

                return true;

            case R.id.share:

                if (outputPath != null) {
                    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                    emailIntent.setType("application/image");
                    emailIntent.setType("message/rfc822");
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"example@example.com"});
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Test Subject");
                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "From SCAN CAMERA");
                    File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "out.pdf");
                    File file = new File(outputPath);
                    Uri path = Uri.fromFile(file);
                    emailIntent.putExtra(Intent.EXTRA_STREAM, path);
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                    Toast.makeText(getApplicationContext(), "Clicked Email " + String.valueOf(id), Toast.LENGTH_SHORT).show();
                }
                else
                    startScan(ScanConstants.PICKFILE_FROM_GALLERY);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
