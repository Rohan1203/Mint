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

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Header;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WithdrawOutputActivity extends AppCompatActivity {
    private static final int STORAGE_CODE =1000 ;
    TextView withdrawAccountNumber;
    TextView transactionType;
    TextView withdrawAvailableBalance;
    TextView withdrawRrn;
    TextView withdrawAmount;
    TextView withdrawDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_withdraw_output);

        withdrawAccountNumber = (TextView) findViewById (R.id.textViewWithdrawAccountNumber);
        transactionType = (TextView) findViewById (R.id.textViewWithdrawTransactionType);
        withdrawRrn = (TextView) findViewById (R.id.textViewWithdrawRRN);
        withdrawAmount = (TextView) findViewById (R.id.textViewWithdrawAmount);
        withdrawDate = (TextView) findViewById (R.id.textViewWithdrawDate);

        Button buttonPrintWithdraw = findViewById (R.id.buttonPrintWithdraw);

        Intent intent = getIntent ();
        String withdrawAccountNo =  intent.getStringExtra ("withdrawAccountNumber");

        String value1 = withdrawAccountNo.substring(1,9);
        String value2 = value1.replace(value1,"******") + withdrawAccountNo.substring(6,9);
        withdrawAccountNumber.setText(value2);

        String withdrawRRN = intent.getStringExtra ("withdrawRrn");
        String withdrawAmount1 = intent.getStringExtra ("withdrawAmount" );
        String withdrawDate1 = intent.getStringExtra ("withdrawDate");

        withdrawAccountNumber.setText (withdrawAccountNo);
        transactionType.setText ("AEPS Withdraw");
        withdrawRrn.setText (withdrawRRN);
        withdrawAmount.setText (withdrawAmount1 + " INR");
        withdrawDate.setText (withdrawDate1);

        buttonPrintWithdraw.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){

                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                        String [] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions, STORAGE_CODE);
                    }
                    else{
                        savePdf();
                    }
                }
                else{
                    savePdf();
                }
            }
        });

    }


    private void savePdf() {
        Document mDoc = new Document();
        String mFileName = new SimpleDateFormat ("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());
        String mFilePath = Environment.getExternalStorageDirectory() + "/" + "Mint" + "/" + "AepsWithdraw_" + mFileName + ".pdf";
        try {
            PdfWriter.getInstance(mDoc, new FileOutputStream (mFilePath));;
            mDoc.open();
            String heading = "-- Transaction Report --";
            String pdfText = withdrawAccountNumber.getText().toString();
            String pdfText1 = withdrawRrn.getText ().toString ();
            String pdfText2 = transactionType.getText ().toString ();
            String pdfText3 = withdrawAmount.getText ().toString ();
            String pdfText4 = withdrawDate.getText ().toString ();

            Rectangle rect = new Rectangle (577, 825, 18, 15);
            rect.enableBorderSide (1);
            rect.enableBorderSide (2);
            rect.enableBorderSide (4);
            rect.enableBorderSide (8);

            rect.setBorderColor (BaseColor.BLACK);
            rect.setBorderWidth (2);

            mDoc.add (rect);

            mDoc.add (new Paragraph (heading));
            mDoc.add(new Paragraph("Account Number : " + pdfText));
            mDoc.add (new Paragraph ("RRN : " + pdfText1));
            mDoc.add (new Paragraph ("Transaction Type" + pdfText2));
            mDoc.add (new Paragraph ("Withdraw Amount : " + pdfText3));
            mDoc.add (new Paragraph ("Withdraw Date : " + pdfText4));
            mDoc.close ();
            Toast.makeText(this, "saved" + mFilePath,Toast.LENGTH_LONG).show();

        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case STORAGE_CODE:{
                if(grantResults.length >  0 && grantResults.length == PackageManager.PERMISSION_GRANTED){
                    savePdf();
                }
                else{
                    Toast.makeText(this,"Permission Denied..!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
