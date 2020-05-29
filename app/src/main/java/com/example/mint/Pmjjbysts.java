package com.example.mint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Pmjjbysts extends AppCompatActivity {
    private static final int STORAGE_CODE=1000;
    TextView acountno;
    TextView nomineename;
    TextView nomineeaadhar;
    TextView scheme_id;
    Button printpmjjby;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;
    TextView currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_pmjjbysts);

        String aadhar_number= getIntent().getStringExtra("aadhar_number");

        acountno = findViewById(R.id.textViewPmjjbyAccountNumber);
        nomineename = findViewById(R.id.textViewPmjjbyNomineeName);
        nomineeaadhar = findViewById(R.id.textViewPmjjbyNomineeAadhar);
        scheme_id = findViewById(R.id.textViewPmjjbySchemeId);
        printpmjjby=findViewById(R.id.printpmjjby);
        currentDate = findViewById (R.id.textViewPmjjbyDate);

        calendar = Calendar.getInstance ();

        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        date = dateFormat.format(calendar.getTime());
        currentDate.setText(date);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.42.103:8080/Mint/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PmjjbyApi pmjjbyapi = retrofit.create(PmjjbyApi.class);
        Call<Pmjjby> call = pmjjbyapi.getAadharnumber(aadhar_number);
        call.enqueue(new Callback<Pmjjby>() {
            @Override
            public void onResponse(Call<Pmjjby> call, Response<Pmjjby> response) {
                if (!response.isSuccessful()) {
                    acountno.setText("Error Code: " + response.code());
                    nomineename.setText("Error Code: " + response.code());
                    nomineeaadhar.setText("Error Code: " + response.code());
                    scheme_id.setText("Error Code: " + response.code());
                    return;
                }


                Pmjjby pmjjby = response.body();
                {
                    acountno.append(" " + pmjjby.getAccount_number());
                    nomineename.append(" " + pmjjby.getNominee_name());
                    nomineeaadhar.append(" " + pmjjby.getNominee_aadhar());
                    scheme_id.append(" " + pmjjby.getScheme_id());

                }
            }

            @Override
            public void onFailure(Call<Pmjjby> call, Throwable t) {
                acountno.setText(t.getMessage());
                nomineename.setText(t.getMessage());
                nomineeaadhar.setText(t.getMessage());
                scheme_id.setText(t.getMessage());

            }


        });
//

        printpmjjby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                        String [] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions,STORAGE_CODE);
                    }
                    else{
                        savePdf1();
                    }
                }
                else{
                    savePdf1();
                }
            }
        });

    }

    public void savePdf1()
    {
        Document mDoc = new Document();
        String mFileName = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());
        String mFilePath = Environment.getExternalStorageDirectory() + "/MINT/PMJJBY_applied" + mFileName + ".pdf";
        try {
            PdfWriter.getInstance(mDoc, new FileOutputStream(mFilePath));
            mDoc.open();
            String heading = "-- Transaction Report --";
            String pdfText = acountno.getText().toString();
            String pdfText1 = nomineename.getText ().toString ();
            String pdfText2 =nomineeaadhar.getText ().toString ();
            String pdfText3 =scheme_id.getText ().toString ();
            String pdfText4 = currentDate.getText ().toString ();


            mDoc.addTitle (String.valueOf (new Paragraph(heading)));
            mDoc.add(new Paragraph("----Transaction Report---- "));
            mDoc.add(new Paragraph("Scheme Id: "+pdfText3));
            mDoc.add(new Paragraph("Scheme Type - PMJJBY "));
            mDoc.add(new Paragraph(" Customer Account Number: " + pdfText));
            mDoc.add (new Paragraph ("Nominee Name : " + pdfText1));
            mDoc.add (new Paragraph ("Nominee Aadhar Number : " + pdfText2));
            mDoc.add (new Paragraph ("Date : " + pdfText4));
            mDoc.close ();
            Toast.makeText(this, "saved" + mFilePath,Toast.LENGTH_LONG).show();

            mDoc.close ();
            Toast.makeText(this, "saved" + mFilePath,Toast.LENGTH_LONG).show();
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_CODE: {
                if (grantResults.length > 0 && grantResults.length == PackageManager.PERMISSION_GRANTED) {
                    savePdf1();
                } else {
                    Toast.makeText(this, "Permission Denied..!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
