package com.qq.weixin.mp.aes;

import java.util.ArrayList;
import java.util.stream.IntStream;

class ByteGroup {
	ArrayList<Byte> byteContainer = new ArrayList<>();

	public byte[] toBytes() {
		byte[] bytes = new byte[byteContainer.size()];
		IntStream.range(0, byteContainer.size()).forEach(i -> bytes[i] = byteContainer.get(i));
		return bytes;
	}

	public ByteGroup addBytes(byte[] bytes) {
		for (byte b : bytes) {
			byteContainer.add(b);
		}
		return this;
	}

	public int size() {
		return byteContainer.size();
	}
}
