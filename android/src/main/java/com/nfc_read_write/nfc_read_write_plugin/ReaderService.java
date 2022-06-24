package com.nfc_read_write.nfc_read_write_plugin;

import android.nfc.tech.MifareClassic;
import android.util.Log;
import android.util.SparseArray;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;


/**
 * 新版NFC读写卡
 */
public class ReaderService {
    private static final String tag = "NewNFCReaderService";
    private static final String CHARSET = "GB2312";
    private byte[] passwordA;
    private byte[] passwordB;
    private byte[]   arrKey  ;
    private byte[] arrIV  ;
    public ReaderService() {
        super();
        String cardPassword = "42374D4C3546";
        passwordA = Tools.HexString2Bytes(cardPassword);
        passwordB = Tools.HexString2Bytes("B10A23D18F20");
    }
    public ReaderService(String cardPasswordA,String cardPasswordB) {
        super();
        this.passwordA = Tools.HexString2Bytes(cardPasswordA);
        this.passwordB = Tools.HexString2Bytes(cardPasswordB);
        
    }
    public void setKV(String uid){
        try {
            this. arrKey = uid.getBytes("UTF-8");
            this. arrIV = new StringBuffer(uid).reverse().toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
    public SparseArray<SparseArray<String>> readAll(MifareClassic mifareClassic,boolean decrypt) throws IOException {
         
        Log.d("TAG", "readAll");
        SparseArray<SparseArray<String>> arrayData = new SparseArray<>();
        try {
            byte[] sectorPassword;
             if (null==passwordA||passwordA.length<=0) {
                 sectorPassword= MifareClassic.KEY_DEFAULT;
            } else {
                sectorPassword= passwordA;
            }
            if (!mifareClassic.isConnected()) {
                mifareClassic.connect();
            }

            for (int i = 0; i < mifareClassic.getSectorCount(); i++) {
                boolean auth = mifareClassic.authenticateSectorWithKeyA(i,sectorPassword);
                if (!auth){
                    mifareClassic.authenticateSectorWithKeyA(i,passwordB);
                }
                arrayData.append(i,Tools.readSector(mifareClassic,i,decrypt,arrKey, arrIV));
            }
        } catch ( Exception e)  {
            Log.e("TAG", "readAll: ", e);
        }finally {
            mifareClassic.close();

        }
        return arrayData;
    }

    
    public SparseArray<SparseArray<String>> readSector(MifareClassic mifareClassic,Integer sectorIndex,boolean decrypt) throws IOException {
        Log.d("TAG", "readSector");

        SparseArray<SparseArray<String>> arrayData = new SparseArray<>();
        try {
            byte[] sectorPassword;
            if (null==passwordA||passwordA.length<=0) {
                sectorPassword= MifareClassic.KEY_DEFAULT;
            } else {
                sectorPassword= passwordA;
            }
            if (!mifareClassic.isConnected()) {
                mifareClassic.connect();
            }


                boolean auth = mifareClassic.authenticateSectorWithKeyA(sectorIndex,sectorPassword);
                if (!auth){
                    mifareClassic.authenticateSectorWithKeyA(sectorIndex,passwordB);
                }

                arrayData.append(sectorIndex,Tools.readSector(mifareClassic,sectorIndex,decrypt,arrKey, arrIV));

        } catch ( Exception e)  {
            Log.e("TAG", "readSector: ", e);
        }finally {
            mifareClassic.close();

        }
        return arrayData;
    }
    public  SparseArray<String>  readBlock(MifareClassic mifareClassic,Integer blockIndex,boolean decrypt) throws IOException {
        //decrypt  encrypt
        Log.d("TAG", "readBlock");
         SparseArray<String> arrayData = new SparseArray<>();
        try {
            byte[] sectorPassword;
            if (null==passwordA||passwordA.length<=0) {
                sectorPassword= MifareClassic.KEY_DEFAULT;
            } else {
                sectorPassword= passwordA;
            }
            if (!mifareClassic.isConnected()) {
                mifareClassic.connect();
            }
            Integer sectorIndex = mifareClassic.blockToSector(blockIndex);
                boolean auth = mifareClassic.authenticateSectorWithKeyA(sectorIndex,sectorPassword);
                if (!auth){
                    mifareClassic.authenticateSectorWithKeyA(sectorIndex,passwordB);
                }
                byte[] bytes = mifareClassic.readBlock(blockIndex);
                String data=  ConvertUtil.byteArrayToString(bytes,0,bytes.length,CHARSET);
            System.out.println("解密前:::"+data);

            if (decrypt){
                    System.out.println("data::"+data+" arrKey::"+arrKey.toString()+" arrIV::"+arrIV);
                    data=DESKeyIVUtil.decrypt(data,arrKey,arrIV);
                }
                arrayData.append(sectorIndex,data);

        } catch ( Exception e)  {
            Log.e("TAG", "readBlock: ", e);

        } finally {
            mifareClassic.close();

        }
        return  arrayData;
    }
    public boolean writeBlockByte(MifareClassic mifareClassic,Integer blockIndex){

        return true;
    }


    public boolean writeBlock(MifareClassic mifareClassic,Integer blockIndex,String data,boolean encrypt) throws Exception {
        try {


                    //decrypt  encrypt
            byte[] sectorPassword;
            if (null==passwordA||passwordA.length<=0) {
                sectorPassword= MifareClassic.KEY_DEFAULT;
            } else {
                sectorPassword= passwordA;
            }
            if (!mifareClassic.isConnected()) {
                mifareClassic.connect();
            }
            if (encrypt){
                data = DESKeyIVUtil.encrypt(data,arrKey,arrIV);

            }


            byte[] byteData = ConvertUtil.bcdStringToBytes(data,0,16);
            //Tools.String2Bytes(data );

            Integer sectorIndex = mifareClassic.blockToSector(blockIndex);
            boolean auth = mifareClassic.authenticateSectorWithKeyA(sectorIndex,sectorPassword);
            if (!auth){
                mifareClassic.authenticateSectorWithKeyA(sectorIndex,passwordB);
            }
//            byte[] data1 = new byte[16];
//
//            data1 = ConvertUtil.bcdStringToBytes(DESKeyIVUtil.encrypt("2",arrKey,arrIV), data1, 0, 8);
//            data1 = ConvertUtil.bcdStringToBytes(DESKeyIVUtil.encrypt("56",arrKey,arrIV), data1, 8, 8);

             mifareClassic.writeBlock( blockIndex,byteData  );
            return  true;

        } catch (  Exception e) {
             Log.e("TAG", "writeMifare: ", e);
             throw  e;
         }finally {
            mifareClassic.close();
        }
    }
 
}

