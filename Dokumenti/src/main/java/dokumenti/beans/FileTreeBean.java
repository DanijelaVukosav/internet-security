package dokumenti.beans;

import java.io.File;


public class FileTreeBean {
	
	private MyDirectory root= new MyDirectory();

	public MyDirectory getRoot() {
		return root;
	}

	public void setRoot(MyDirectory root) {
		this.root = root;
	}
	
	public MyDirectory generateDirectoryTree(MyDirectory tree, String rootFolder)
	{
		
		File directory = new File(rootFolder); 
		tree.setDirectoryName(directory.getName());
		tree.setDirectoryFile(directory);
		for(File file:directory.listFiles())
		{
			
			if(file.isDirectory())
			{
				MyDirectory noviDirektoriju=new MyDirectory();
				tree.getDirectories().add(noviDirektoriju);
				generateDirectoryTree(noviDirektoriju, file.getAbsolutePath());
			}
			else {
				tree.getFiles().add(file);
			}
		}
		
		return tree;
	}
	public static StringBuilder napraviHTMLKodStabla(MyDirectory root, StringBuilder retValue,String operacije)
	{

		String partOfPath=File.separator+"Dokumenti"+File.separator+"WEB-INF"+File.separator+"CR"+File.separator;
		String absPathDir= root.getDirectoryFile().getAbsolutePath();
		String idDirektorijuma="";

		if(root.getDirectoryFile().getParentFile().getName().equals("WEB-INF"))
		{
			 idDirektorijuma="_";
		}
		else 
		{
			 idDirektorijuma=absPathDir.substring(absPathDir.indexOf(partOfPath)+partOfPath.length());
			
		}
		
		retValue.append("<li  id=\""+ idDirektorijuma+"\" role=\"treeitem\" aria-expanded=\"true\" class=\"folder\"><span>"+ root.getDirectoryName()+"</span>");
		retValue.append("<ul role=\"group\">");
		for(File datoteka: root.getFiles()) {
			
			String absPath= datoteka.getAbsolutePath();
			retValue.append("<li role=\"treeitem\" id=\""+absPath.substring(absPath.indexOf(partOfPath)+partOfPath.length())+"\" class=\"doc\">"+datoteka.getName()); 
			retValue.append("</li>");
			
		}
		for(MyDirectory dir:root.getDirectories())
			napraviHTMLKodStabla(dir, retValue,operacije);
		retValue.append("</ul></li>");
		return retValue;
	}
      

}
