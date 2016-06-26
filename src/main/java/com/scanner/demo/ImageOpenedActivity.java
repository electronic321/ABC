package com.scanner.demo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by ADMIN on 6/23/2016.
 */
public class ImageOpenedActivity extends Activity {
    ImageView imageView;
    String received;
    private static final int REQUEST_CODE = 99;
    private Button pdfConverter, openPDFConverter;
    //    String outputPath, inputPath;
    String outputPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                received = null;
            }
            else {
                received = extras.getString("Image");
            }
        }
        else {
            received = (String) savedInstanceState.getSerializable("Image");
        }
        setContentView(R.layout.imageopened);

        pdfConverter = (Button) findViewById(R.id.PDF);
        openPDFConverter = (Button) findViewById(R.id.openPDF);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageURI(Uri.parse(received));
        final String inputPath = received;
        outputPath = received + "PDF.pdf";
        openPDFConverter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File openfile = new File(outputPath);

                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(openfile), "application/pdf");
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e("Errorrrr ::::", outputPath);
                }


            }
        });

        pdfConverter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertImage(inputPath, outputPath);
            }
        });
    }

    private void convertImage(String imagePath, String outputPath) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();
            Image image = Image.getInstance(imagePath);
            image.setAbsolutePosition(0, 0);
//            image.scalePercent(24);
            image.scaleAbsolute((float) 2549 * (float) .24, (float) 3304 * (float) .24);
//            image.setDpi(100, 100);
            image.scaleToFit((float) 2549 * (float) .24, (float) 3304 * (float) .24);

            document.add(image);
            document.close();
            Toast.makeText(getApplicationContext(), "Converting image to PDF finished... Generated PDF saved in " + outputPath, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "An error Occured... " + outputPath, Toast.LENGTH_LONG).show();

        }
    }


}
