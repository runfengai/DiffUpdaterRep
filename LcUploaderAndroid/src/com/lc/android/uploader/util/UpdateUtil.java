package com.lc.android.uploader.util;

import io.dcloud.application.DCloudApplication;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.StandardFeature;
import io.dcloud.common.util.JSUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.json.JSONArray;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

/**
 * Java utils 实现的Zip工具
 * 
 * @author zxr
 */
public class UpdateUtil extends StandardFeature {
	static final String TAG = UpdateUtil.class.getSimpleName();
	private final int BUFF_SIZE = 10 * 1024;
	// APPID
	private static final String APP_ID = "H5C22DBD6";

	public void onStart(Context pContext, Bundle pSavedInstanceState,
			String[] pRuntimeArgs) {
	}

	/**
	 * 如：sdcard/Android/data/com.lc.android.lcuploader/
	 * 
	 * @return
	 */
	public static String getRootPath() {
		File sd = Environment.getExternalStorageDirectory();
		Log.d(TAG, "sd=" + sd.getAbsolutePath());
		String proRootPath = sd.getAbsolutePath()
				+ File.separator
				+ "Android"
				+ File.separator
				+ "data"
				+ File.separator
				+ DCloudApplication.getInstance().getApplicationContext()
						.getPackageName() + File.separator;
		return proRootPath;
	}

	public static String getProPath() {
		return getRootPath() + "apps" + File.separator + APP_ID
				+ File.separator + "www" + File.separator;
	}

	/**
	 * 解压是耗时操作，所以放到线程中， 在子线程中传递给JS的回调，DCloud已经给回调到主线程中，所以不用担心线程异常。 解压缩一个文件
	 * 考虑线程池的问题，但是目前就这个功能用到，所以感觉没必要维护线程池，暂时不加了。
	 * 
	 * @param zipFile
	 *            压缩文件
	 * @param folderPath
	 *            解压缩的目标目录
	 * @throws IOException
	 *             当解压缩过程出错时抛出
	 */
	public void upZipFile(final IWebview pWebview, final JSONArray array) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String zipFilePath = getRootPath() + array.optString(1);
				String folderPath = getProPath();
				Log.d(TAG, "压缩文件：" + zipFilePath);
				Log.d(TAG, "目标文件：" + folderPath);
				File zipFile = new File(zipFilePath);
				File desDir = new File(folderPath);
				Log.d(TAG, "desDir.exists()=" + desDir.exists());
				Log.d(TAG, "zipFile.exists()=" + zipFile.exists());
				if (!desDir.exists()) {
					desDir.mkdirs();
				}
				if (!zipFile.exists()) {
					Log.d(TAG, "压缩文件不存在！");
					return;

				}
				boolean isSuccess = true;
				ZipFile zf = null;
				try {
					zf = new ZipFile(zipFile);
					for (Enumeration<?> entries = zf.entries(); entries
							.hasMoreElements();) {
						ZipEntry entry = ((ZipEntry) entries.nextElement());
						if (!entry.isDirectory()) {
							InputStream in = zf.getInputStream(entry);
							String str = folderPath + File.separator
									+ entry.getName();
							str = new String(str.getBytes("8859_1"), "GB2312");
							Log.d(TAG, "str=" + str);
							File desFile = new File(str);
							if (!desFile.exists()) {
								File fileParentDir = desFile.getParentFile();
								if (!fileParentDir.exists()) {
									fileParentDir.mkdirs();
								}
								desFile.createNewFile();
							}
							Log.d(TAG, "" + desFile.getName());
							OutputStream out = new FileOutputStream(desFile);
							byte buffer[] = new byte[BUFF_SIZE];
							int realLength;
							while ((realLength = in.read(buffer)) > 0) {
								out.write(buffer, 0, realLength);
							}
							out.close();
							in.close();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					isSuccess = false;
				} finally {
					if (null != zf) {
						try {
							zf.close();
						} catch (IOException e) {
							e.printStackTrace();
							isSuccess = false;
						}
					}
				}

				final JSONArray newArray = new JSONArray();
				newArray.put(isSuccess);
				newArray.put(isSuccess ? "文件更新成功！" : "文件更新失败！");
				JSUtil.execCallback(pWebview, array.optString(0), newArray,
						isSuccess ? JSUtil.OK : JSUtil.ERROR, false);
			}
		}).start();

	}

	/**
	 * 替换文件,本人暂时没用到，一开始构思的时候想到通过先解压再替换，但是后来发现如果不用对比，可以直接一步搞定了。
	 * 
	 * @return
	 */
	public boolean replaceFile(IWebview pWebview, JSONArray array) {
		String newDirPath = array.optString(1);
		String originDirPath = array.optString(2);
		boolean isSuccess = replace(newDirPath, originDirPath);
		final JSONArray newArray = new JSONArray();
		newArray.put(isSuccess ? "替换成功！" : "替换失败！");
		JSUtil.execCallback(pWebview, array.optString(0), newArray,
				isSuccess ? JSUtil.OK : JSUtil.ERROR, false);
		return isSuccess;
	}

	/**
	 * 替换文件
	 * 
	 * @param newDirPath
	 * @param originPath
	 */
	private boolean replace(String newDirPath, String originDirPath) {
		File newDir = new File(newDirPath);// 刚解压的文件
		if (!newDir.exists()) {
			Log.d(TAG, "下载文件不存在");
			return false;
		}
		File oriDir = new File(originDirPath);
		if (!oriDir.exists()) {
			Log.d(TAG, "源文件不存在");
			return false;
		}
		// 根据newDir，找到其子目录，并将子目录中的内容替换到源文件夹中。
		File[] list = newDir.listFiles();
		for (File file : list) {
			File oriFile = new File(originDirPath + File.separator
					+ file.getName());
			if (file.isDirectory()) {// 文件夹的情况
				Log.d(TAG, file.getAbsolutePath());
				if (!oriFile.exists()) {
					oriFile.mkdirs();
				}
				replace(file.getAbsolutePath(), oriFile.getAbsolutePath());
			} else if (file.isFile()) {// 文件
				Log.d(TAG, file.getName());
				// 不管存不存在，直接拷贝
				BufferedInputStream newDirBIS = null;
				BufferedOutputStream oriBOS = null;
				try {
					newDirBIS = new BufferedInputStream(new FileInputStream(
							file));
					oriBOS = new BufferedOutputStream(new FileOutputStream(
							oriFile));
					byte[] b = new byte[2048];
					int len = -1;
					while (-1 != (len = newDirBIS.read(b))) {
						oriBOS.write(b, 0, len);
					}

				} catch (FileNotFoundException e) {
					Log.d(TAG, "文件不存在");
					return false;

				} catch (IOException e) {
					Log.d(TAG, "io 异常");
					return false;
				} finally {

					try {
						if (null != oriBOS) {
							oriBOS.close();
						}
						if (null != newDirBIS) {
							newDirBIS.close();
						}
					} catch (IOException e) {
						e.printStackTrace();

					}
				}
			}

		}

		return true;
	}

}