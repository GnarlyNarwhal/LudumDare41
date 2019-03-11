package com.gnarly.game;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.InputMismatchException;

public class InputReader {
		
		private InputStream stream;
		
		public InputReader(String path) { 
			try {
				this.stream = new FileInputStream(path);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		public InputReader(InputStream stream) { 
			this.stream = stream;
		}
		
		public int read() {
			try {
				return stream.read();
			} catch (IOException e) {
				e.printStackTrace();
				return 0;
			}
		}
		
		public char nextChar() {
			char c = (char) read();
			while(Character.isWhitespace(c)) c = (char) read();
			return c;
		}
		
		public String next(char delimiter) {
			StringBuilder string = new StringBuilder();
			char c = (char) read();
			while(Character.isWhitespace(c)) c = (char) read();
			while(c != delimiter) {
				string.append(c);
				c = (char) read();
			}
			return string.toString();
		}
		
		public String nextLine() {
			StringBuilder string = new StringBuilder();
			char c = (char) read();
			while(Character.isWhitespace(c)) c = (char) read();
			while(c != '\n') {
				string.append(c);
				c = (char) read();
			}
			return string.toString();
		}
		
		public int nextInt() {
			int num = 0;
			int sign = 1;
			char c = (char) read();
			while(Character.isWhitespace(c)) c = (char) read();
			if(c == '-') {
				sign = -1;
				c = (char) read();
			}
			else if(c < '0' || c > '9')
				throw new InputMismatchException();
			while(c >= '0' && c <= '9') {
				num *= 10;
				num += c - '0';
				c = (char) read();
			}
			return num * sign;
		}
		
		public int[] nextIntArray(int length) {
			int[] nums = new int[length];
			for (int i = 0; i < length; i++)
				nums[i] = nextInt();
			return nums;
		}
		
		public void close() {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}