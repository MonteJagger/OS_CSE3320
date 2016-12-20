The following code is built in java. In the command line, go to the following directory where the PortableFileSystem.java file is located. To run the code enter:

	javac PortableFileSystem.java

	java PortableFileSystem 




Creating File System
To create a file system (disk) with number of blocks size, each 128 bytes, enter creates followed by a number great than or equal 10. For example:

	createfs 10

Formatting File System
To format the file system, enter:

	formatfs

Saving Disk
To save the "disk" image in a file "name", enter savefs followed by a name, for example:
	
	savefs disk01

Opening Disk
To open the "disk" image in a file "name", enter openfs followed by a name, for example:
	
	openfs disk01

Listing
To list files (and other meta-information) in a file system, enter:

	list

Removing File
To remove a file in the file system, make sure you are in the correct file system and enter remove followed by the file name. For example:

	remove sample.txt

Adding File from OS to Disk
To add a file from the (Host) OS in the disk file system, make sure you are in the correct disk and enter put followed by the file name you want to add. For example:

	put sample.txt

Copying File from Disk to OS
To copy a file from the disk file system to the (Host) OS, enter get followed by the file name you want to copy to the (host) OS. For example:

	get sample.txt

Displaying the Owner of the Files
To display the user that owns the user's files, enter:

	user

Link/Unlink Files
To link files together make sure the all the wanted files are in the disk, once that is done, enter link followed by the files needed for linking, for example:

	link sample1.txt sample2.txt sample3.txt

sample1.txt, sample2.txt, and sample3.txt should already be added in the disk.

To unlink files together make sure the  files are in the disk, then enter unlink followed by the files needed for unlinking, for example:

	unlink sample1.txt sample2.txt sample3.txt


