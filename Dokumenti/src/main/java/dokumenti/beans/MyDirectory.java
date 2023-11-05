package dokumenti.beans;

import java.io.File;
import java.util.ArrayList;

public class MyDirectory {
	private String directoryName;
	private File directoryFile=new File("Greska");
	private ArrayList<MyDirectory> directories=new ArrayList<>();
	private ArrayList<File> files=new ArrayList<File>();
	
	
	
	public MyDirectory() {
		super();
	}
	
	public MyDirectory(String directoryName, ArrayList<MyDirectory> directories, ArrayList<File> files) {
		super();
		this.directoryName = directoryName;
		this.directories = directories;
		this.files = files;
	}
	public MyDirectory(String directoryName,File directoryFile) {
		super();
		this.directoryName = directoryName;
		this.directoryFile=directoryFile;
	}

	public String getDirectoryName() {
		return directoryName;
	}
	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}
	public ArrayList<MyDirectory> getDirectories() {
		return directories;
	}
	public void setDirectories(ArrayList<MyDirectory> directories) {
		this.directories = directories;
	}
	public ArrayList<File> getFiles() {
		return files;
	}
	public void setFiles(ArrayList<File> files) {
		this.files = files;
	}
	
	public File getDirectoryFile() {
		return directoryFile;
	}

	public void setDirectoryFile(File directoryFile) {
		this.directoryFile = directoryFile;
	}

	@Override
	public String toString()
	{
		StringBuilder stringBuilder=new StringBuilder("root "+directoryName+"\n");
		napraviStablo(stringBuilder,this);
		return stringBuilder.toString();
	}

	private void napraviStablo(StringBuilder string, MyDirectory root) {
		string.append("\nRoot folder :-> "+root.directoryName);
		for(File file:root.files)
			string.append("File : "+file.getName());
		for(MyDirectory dir:root.directories)
			napraviStablo(string,dir);
	}
	
}
