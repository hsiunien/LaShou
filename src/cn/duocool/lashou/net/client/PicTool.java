package cn.duocool.lashou.net.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Environment;

public class PicTool {
	
	/**
	 * 压缩文件后存放到SD卡中
	 * @param rawBitmap
	 * @param fileName
	 * @param quality
	 * @return
	 */
	public static String compressAndSaveBitmapToSDCard(
			Bitmap rawBitmap,
			String fileName,
			int quality){
		String saveFilePath= Environment.getExternalStorageDirectory()+File.separator+fileName; 
		
		File saveFile=new File(saveFilePath);
		if (saveFile.exists()) {
			saveFile.delete();
		}
		
		try { 
				saveFile.createNewFile(); 
				FileOutputStream fileOutputStream=new FileOutputStream(saveFile); 
				if (fileOutputStream!=null) { 
					//imageBitmap.compress(format, quality, stream); 
					//把位图的压缩信息写入到一个指定的输出流中 
					//第一个参数format为压缩的格式 
					//第二个参数quality为图像压缩比的值,0-100.0 意味着小尺寸压缩,100意味着高质量压缩 
					//第三个参数stream为输出流 
					rawBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream); 
				} 
				fileOutputStream.flush(); 
				fileOutputStream.close(); 
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
		
		return saveFilePath;
	}
}