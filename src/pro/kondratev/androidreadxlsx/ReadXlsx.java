package pro.kondratev.androidreadxlsx;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class ReadXlsx extends Activity {
	
	static final int BUF_SIZE = 8 * 1024;
	protected EditText output;
	protected String outputString;
	protected Handler handler;
	protected InputStream xlsxFileStream;
	protected ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_xlsx);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Lock portrait orientation
        handler = new Handler();
        output = (EditText) findViewById(R.id.outputText);  
        //output.setKeyListener(null);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setProgress(0);
        progress.setMax(100);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.read_xlsx, menu);
        return true;
    }
    
    public void onPickXlsxButtonClick(View view) {
		Toast.makeText(this, "Please select a file in XLSX format", Toast.LENGTH_LONG).show();
		Intent intent2Browse = new Intent();
		intent2Browse.addCategory(Intent.CATEGORY_OPENABLE);
		intent2Browse.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		intent2Browse.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent2Browse,2);
    }
    
    public void onReadSimpleXlsxButtonClick(View view) {
		Toast.makeText(this, "Reading simple.xlsx", Toast.LENGTH_LONG).show();
		try {
			File simpleXlsxInternalStoragePath = new File(getDir("dex", Context.MODE_PRIVATE),"simple.xlsx");
			BufferedInputStream bis = new BufferedInputStream(getAssets().open("simple.xlsx"));
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(simpleXlsxInternalStoragePath));
			byte[] buf = new byte[BUF_SIZE];
			int len;
			while((len = bis.read(buf, 0, BUF_SIZE)) > 0) {
				bos.write(buf, 0, len);
			}
			bos.close();
			bis.close();
			InputStream fileStream = new FileInputStream(simpleXlsxInternalStoragePath);
			readXLSX(fileStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 2 && resultCode == RESULT_OK) {
			ContentResolver cR = this.getContentResolver();
			Uri fileUri = data.getData();
			try {
				InputStream fileStream = cR.openInputStream(fileUri);
				readXLSX(fileStream);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}			
		}
	}

	/**
	 * print message to the output TextView
	 * @param str
	 */
	private void appendOutput(String str) {
		final String string = str;
		handler.post(new Runnable() {
			public void run() {				
				if (output.length()>8000) {
					CharSequence fullOutput = output.getText();
					fullOutput = fullOutput.subSequence(5000,fullOutput.length());
					output.setText(fullOutput);
					output.setSelection(fullOutput.length());
				}
				output.append(string);
			}
		});
	}
	
	/**
	 * read XLSX from file stream
	 * @param fileStream
	 */
	private void readXLSX(InputStream fileStream) {
		output.append("reading xlsx file...\n");
		progress.setProgress(1);
		xlsxFileStream = fileStream;
		new Thread(new Runnable(){
			public void run() {
				try {
					Workbook workbook = new XSSFWorkbook(xlsxFileStream);
					progress.setProgress(5);
					appendOutput("XSSFWorkbook instance created...\n");
					Sheet sheet = workbook.getSheetAt(0);
					appendOutput("getSheetAt(0) done...\n");
					int rowsCount = sheet.getPhysicalNumberOfRows();
					progress.setProgress(10);
					appendOutput("rows count:"+rowsCount+"\n");
					FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
					for (int r = 0; r<rowsCount; r++) {
						Row row = sheet.getRow(r);
						int cellsCount = row.getPhysicalNumberOfCells();
						for (int c = 0; c<cellsCount; c++) {
							String value = null;
							try {
								Cell cell = row.getCell(c);
								CellValue cellValue = formulaEvaluator.evaluate(cell);
								switch (cellValue.getCellType()) {
									case Cell.CELL_TYPE_BOOLEAN:
										value = ""+cellValue.getBooleanValue();
										break;
									case Cell.CELL_TYPE_NUMERIC:
										double numericValue = cellValue.getNumberValue();
										if(HSSFDateUtil.isCellDateFormatted(cell)) {
											double date = cellValue.getNumberValue();
											SimpleDateFormat formatter =
													new SimpleDateFormat("dd/MM/yy");
											value = formatter.format(HSSFDateUtil.getJavaDate(date));
										} else {
											value = ""+numericValue;
										}
										break;
									case Cell.CELL_TYPE_STRING:
										value = ""+cellValue.getStringValue();
										break;
									default:
								}
							} catch (NullPointerException nE) {
							}
							String cellInfo = "r:"+r+"; c:"+c+"; v:"+value;
							//System.out.println(cellInfo);
							appendOutput(cellInfo+"\n");
						}					
						int rowPointer = r;
						float fProgress = (float)rowPointer/(float)rowsCount*(float)89;
						int progressVal = Math.round(10+fProgress);
						progress.setProgress(progressVal);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}	
				
				progress.setProgress(100);
				appendOutput("finished reading xlsx file\n");
			}
		}).start();
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
