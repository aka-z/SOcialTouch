package fr.socialtouch.android.activity;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;
import fr.socialtouch.android.R;

public class SyncActivity extends Activity {

	// max byte size
	public static final int TAG_SIZE = 1504;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.matching);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    	
	    Intent intent = getIntent();
	    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	    
	    
	    if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {	        
	    	// tag détecté	 
	    	MifareClassic mifare = MifareClassic.get(tag);
	    	// on peut lire et écrire ici
	    	try {
				String data = readFromTag(mifare);
				Log.d("SocialTouch","Tag data = " + data);
				Log.d("SocialTouch","Clear tag data");
				clearTag(mifare);
				Log.d("Social Touch","Write hello world to tag");
				writeToTag(mifare, "Hello World!", false);				
			} catch (IOException e) {
				// oh no !
				e.printStackTrace();
			}    	
	    }
	}
	
	public void clearTag(MifareClassic mifare) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < TAG_SIZE; i++)
		{
			sb.append(" ");
		}
		writeToTag(mifare, sb.toString(), false);		
	}
	
	//TODO append not implemented
	public void writeToTag(MifareClassic mifare, String inData, boolean append) throws IOException
	{	    
		mifare.connect();	
	    boolean auth = false;	    
	    boolean writeEnd = false;
	    int sector = 0;
	    int block = 0;	    
	    byte[] value  = inData.getBytes( Charset.forName("ASCII") );
	    int end = value.length + (MifareClassic.BLOCK_SIZE - value.length%MifareClassic.BLOCK_SIZE);
	    byte[] toWrite = new byte[MifareClassic.BLOCK_SIZE];        
	    
		for (int i=0; i<end; i++) {
			//Change sector every 3 blocks, start at sector 1
			if(i%(3*MifareClassic.BLOCK_SIZE) == 0) 
			{			
				sector++;
				auth = mifare.authenticateSectorWithKeyB(sector,MifareClassic.KEY_DEFAULT); // A = NFC FORUM B = DEFAULT
				if(!auth)
					throw new IOException("Cannot authenticate sector");				
				block = 0;
			}
			
        	if(i>=value.length)
        	{
        		writeEnd = true;
        		toWrite[i%MifareClassic.BLOCK_SIZE] = 0;
        	}else { 
        		toWrite[i%MifareClassic.BLOCK_SIZE] = value[i];
        	}
        	if((i+1)%MifareClassic.BLOCK_SIZE == 0) 
        	{
				BigInteger bi = new BigInteger(toWrite);
				String hexrepresentation = bi.toString(16); 
				Log.d("NFC WRITER","Write " + hexrepresentation + " to sector " + sector + " block " + block + " aka " + (block + mifare.sectorToBlock(sector)));
				mifare.writeBlock(block + mifare.sectorToBlock(sector), toWrite);
				block++;
             }		              		   
		}		  
	    mifare.close();	   
	}
	
	public String readFromTag(MifareClassic mifare) throws IOException
	{
		mifare.connect();    
		
		StringBuilder sb = new StringBuilder();
		int sector = 0;
		boolean auth = false;
		for (int i=4; i<TAG_SIZE/MifareClassic.BLOCK_SIZE; i++) {
			if(i%4 == 0){
				sector++;
				auth = mifare.authenticateSectorWithKeyB(sector,MifareClassic.KEY_DEFAULT);
				if(!auth)
					throw new IOException("Cannot authenticate to sector " + sector);
			}
			sb.append( new String(mifare.readBlock(i),Charset.forName("ASCII"))); 
		}
		mifare.close();
		return sb.toString();	
	}
	
    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);        
    }
}
