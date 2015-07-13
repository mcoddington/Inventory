package com.caci.inventory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Created by mikec_000 on 6/26/2015.
 */
public class ZxingBarCodeDemoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zxing_scan);

        TestDataUtility.createTestData(this);

        Button scanBtn = (Button) findViewById(R.id.btnScan);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Reset previous scan results
                TextView resultTxt = (TextView)findViewById(R.id.resultText);
                resultTxt.setText("");
                // Perform action on click
                IntentIntegrator integrator = new IntentIntegrator(ZxingBarCodeDemoActivity.this);
                integrator.initiateScan();
            }
        });
    }

    /**
     * Handles result from bar code scan.  If result found, searches data for a match.
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        TextView resultTxt = (TextView)findViewById(R.id.resultText);
        if (scanResult != null) {
            String barCodeContent = scanResult.getContents();
            resultTxt.append("Barcode decoded: "+barCodeContent+"\n");
            TestDataUtility.searchForData(ZxingBarCodeDemoActivity.this, barCodeContent, resultTxt);
        } else {
            resultTxt.append("Bar Code could not be scanned.\n");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
