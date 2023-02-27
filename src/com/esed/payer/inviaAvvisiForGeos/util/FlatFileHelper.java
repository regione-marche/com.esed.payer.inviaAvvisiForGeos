package com.esed.payer.inviaAvvisiForGeos.util;

import java.util.*;

public class FlatFileHelper {
	
	public enum FlatFileFieldType { Constant, Numeric, String }
	
	public static class FlatFileField {
		private String name;
		public String getName() {
			return name;
		}

		private FlatFileFieldType type;
		public FlatFileFieldType getType() {
			return type;
		}
				
		private int start;
		public int getStart() {
			return start;
		}

		private int length;
		public int getLength() {
			return length;
		}

		private Object value;		
		public Object getValue() {
			return value;
		}
		public String getString() {
			return (String)value;
		}
		public int getInteger() {
			return Integer.parseInt(getString());
		}
		
		public void setValue(Object value) {
			this.value = value;
		}

		private boolean required;
		public boolean getRequired() {
			return required;
		}

		public FlatFileField(String name, FlatFileFieldType type, int start, int length, boolean required) {
			super();
			this.name = name;
			this.type = type;
			this.start = start;
			this.length = length;
			this.required = required;
		}
		
		public FlatFileField(String name, FlatFileFieldType type, int start, int length, boolean required, Object value) {
			super();
			this.name = name;
			this.type = type;
			this.start = start;
			this.length = length;
			this.required = required;
			this.value = value;
		}
	}

	ArrayList<FlatFileField> fields = new ArrayList<FlatFileField>(); 
		
	public void clear() {
		fields = new ArrayList<FlatFileField>();
	}
	
	public void reset() {
		for(FlatFileField field : fields){
			if (field.getType() != FlatFileFieldType.Constant) {
				field.value = null;
			}
		}
	}

	public void parse(String buffer) {
		for(FlatFileField field : fields){
			String value = buffer.substring(field.getStart()-1, field.getStart()+field.getLength()-1);
			field.setValue(value.trim());
		}
	}
	
	public boolean validate() {
		for(FlatFileField field : fields){
			if (field.getType() != FlatFileFieldType.Constant && field.value == null) {
				return false;
			}
		}
		return true;
	}
	
	public int size() {
		int totalSize = 0;
		for(FlatFileField field : fields){
			totalSize += field.length;
		}
		return totalSize;
	}

	public ArrayList<FlatFileField> getFields() {
		return fields;
	}

	public void addField(FlatFileField field) {
		fields.add(field);
	}
	
	public FlatFileField addField(String name, FlatFileFieldType type, int start, int lenght, boolean required) {
		FlatFileField field = new FlatFileField(name, type, start, lenght, required); 
		fields.add(field);
		return field;
	}
	
	public FlatFileField addField(String name, FlatFileFieldType type, int start, int lenght, boolean required, Object value) {
		FlatFileField field = new FlatFileField(name, type, start, lenght, required, value); 
		fields.add(field);
		return field;
	}
	
	public void removeField(FlatFileField field) {
		fields.remove(field);
	}

	public void setValue(String name, Object value) {
		FlatFileField field = getField(name);
		if (field != null){
			field.setValue(value);
		}
	}
	
	public FlatFileField getField(String name) {
		for(FlatFileField field : fields){
			if (field.getName() == name){
				return field;
			}
		}
		return null;
	}
	
	public String toString() {
		
		StringBuilder retval = new StringBuilder();
		
		for(FlatFileField field : fields){
			
			FlatFileFieldType type = field.getType();
			int length = field.getLength();
			Object value = String.format("%s", field.getValue()).replaceAll("null", "");
			
			String result = value.toString().trim();
			
			if (type == FlatFileFieldType.Constant || type == FlatFileFieldType.String) {
				while(result.length() < length) {
					result += " "; 
				}
			}

			if (type == FlatFileFieldType.Numeric) {
				while(result.length() < length) {
					result = "0" + result; 
				}
			}

			retval.append(result.substring(0, length));
			
			//System.out.println(field.getName() + " " + retval.toString().length());
		}
		
		return retval.toString();
	}
}
