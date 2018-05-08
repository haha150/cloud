package org.cloud;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import com.google.api.gax.paging.Page;
import com.google.cloud.ReadChannel;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class App {

	private static final String BUCKETNAME = "alikthbucket";

	public static void main(String[] args) {
		Storage storage = StorageOptions.getDefaultInstance().getService();
		//uploadmultipleOneGBFiles(storage, 1);
		downloadmultipleOneGBFiles(storage, 1);
	}

	private static void uploadmultipleOneGBFiles(Storage storage, int tries) {
		for (int i = 0; i < tries; i++) {
			try {
				final int j = i;
				new Thread(() -> {
					uploadOneGBFile(storage, j);
				}).start();
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void downloadmultipleOneGBFiles(Storage storage, int tries) {
		for (int i = 0; i < tries; i++) {
			try {
				final int j = i;
				new Thread(() -> {
					downloadOneGBFile(storage, j);
				}).start();
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static void uploadOneGBFile(Storage storage, int filler) {
		Page<Bucket> buckets = storage.list();

		for (Bucket b : buckets.iterateAll()) {
			if (b.getName().equalsIgnoreCase(BUCKETNAME)) {
				File file = new File("D:\\Downloads\\1GB.zip");
				BlobId blobId = BlobId.of(BUCKETNAME, filler + "-" + file.getName());
				InputStream inputStream = null;
				try {
					inputStream = new FileInputStream(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
				long startTime = System.nanoTime();
				WriteChannel writer = null;
				try {
					writer = storage.writer(blobInfo);
					int limit;
					byte[] arr = new byte[8192];
					while ((limit = inputStream.read(arr)) >= 0) {
						writer.write(ByteBuffer.wrap(arr, 0, limit));
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					long stopTime = System.nanoTime();
					long result = TimeUnit.SECONDS.convert(stopTime - startTime, TimeUnit.NANOSECONDS);
					System.out.println("File: " + filler + " took: " + result + " seconds to upload.");
					try {
						inputStream.close();
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private static void downloadOneGBFile(Storage storage, int filler) {
		String fileName = filler + "-" + "1GB.zip";
		BlobId blobId = BlobId.of(BUCKETNAME, fileName);
		Blob blob = storage.get(blobId);
		if (blob == null) {
			System.out.println("No such object");
			return;
		}
		File file = new File("D:\\Downloads\\" + fileName);
		long startTime = System.nanoTime();
		if (blob.getSize() < 1_000_000) {
			// Blob is small read all its content in one request
			byte[] content = blob.getContent();
			try {
				FileUtils.writeByteArrayToFile(file, content);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				long stopTime = System.nanoTime();
				long result = TimeUnit.SECONDS.convert(stopTime - startTime, TimeUnit.NANOSECONDS);
				System.out.println("File: " + filler + " took: " + result + " seconds to upload.");
			}
		} else {
			// When Blob size is big or unknown use the blob's channel reader.
			try (ReadChannel reader = blob.reader()) {
				WritableByteChannel channel = Channels
						.newChannel(new FileOutputStream(file));
				ByteBuffer bytes = ByteBuffer.allocate(64 * 1024);
				while (reader.read(bytes) > 0) {
					bytes.flip();
					channel.write(bytes);
					bytes.clear();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				long stopTime = System.nanoTime();
				long result = TimeUnit.SECONDS.convert(stopTime - startTime, TimeUnit.NANOSECONDS);
				System.out.println("File: " + filler + " took: " + result + " seconds to upload.");
			}
		}
	}
}
