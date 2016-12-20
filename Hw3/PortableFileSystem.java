//  Hiumathy Lam 1001139731
//  CSE 3320 Operating Systems Spring 2016
//  Programming Assignment 3
//  File System Internals

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class PortableFileSystem {

    // saves the meta data of open disk and holds data for new disk to be created.
    public static class FileSystem{
        public int metaBlocks = 1;
        public int numFNT;
        public int numSavedBlocks;
        public int totNumBlocks;
        public int freeBlocks;
        public String name;
        public String date;
        public String time;
    }
    //openedFS:  used to hold the opened file and raf to hold the RandomAccessFile pointing to the opened file
    static File openedFS = null;
    static RandomAccessFile raf = null;

    //fs will save the data for the disk to be created and metaData will save the metaData of the opened file
    static FileSystem fs = new FileSystem();
    static FileSystem metaData = new FileSystem();

    static byte [] nullB = new byte[128];

    static boolean isCreated = false;
    static boolean isFormated = false;
    static boolean isOpen = false;

    //format the newly created FS to specify its FNT size(which is 22% of total size) etc
    public static void formatFS(){
        int fnt = (int)(fs.totNumBlocks * 0.22); //FNT
        fs.numFNT = fnt;
        fs.numSavedBlocks = fs.totNumBlocks - (fs.metaBlocks + fs.numFNT); // total - (metaBlocks + numberFNTs)
        fs.freeBlocks = fs.numSavedBlocks;
        System.out.println("File System has been formatted.");
    }

    //save the newly created FS to the hard disk
    public static void saveFS(String name){
        fs.name = name; // file systems name
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss"); // create date
        String [] t = df.format(new Date()).split(" ");
        fs.date = t[0];
        fs.time = t[1];

        try{
            File f = new File(name);
            RandomAccessFile ram = new RandomAccessFile(f,"rw");
            ram.seek(0);
            ram.writeInt(fs.totNumBlocks);
            ram.writeInt(fs.metaBlocks);
            ram.writeInt(fs.numFNT);
            ram.writeInt(fs.numSavedBlocks);
            ram.writeInt(fs.freeBlocks);
            String utfName = new String(fs.name.getBytes("UTF-8"));
            ram.writeUTF(utfName);
            String utfDate = new String(fs.date.getBytes("UTF-8"));
            ram.writeUTF(utfDate);
            String utfTime = new String(fs.time.getBytes("UTF-8"));
            ram.writeUTF(utfTime);

            int j = fs.metaBlocks * 128;
            for(int i=0 ; i<fs.numFNT ; i++){
                //2 FNT entries in each block
                ram.seek(j);
                ram.writeUTF(new String("".getBytes("UTF-8")));
                j += 64;

                ram.seek(j);
                ram.writeUTF(new String("".getBytes("UTF-8")));
                j += 64;

            }
            for(int i=0 ; i<fs.numSavedBlocks ; i++){
                ram.write(nullB);
            }

            ram.close();
            System.out.println(name+ " has been saved successfully into the File System");
        }
        catch(Exception e){
            System.out.println("Error: File System could not be saved");
        }
    }

    //load metaData of opened FS to the class variable meta data
    public static boolean loadMeta(){
        try{
            raf.seek(0);
            metaData.totNumBlocks = raf.readInt();
            metaData.metaBlocks = raf.readInt();
            metaData.numFNT = raf.readInt();
            metaData.numSavedBlocks = raf.readInt();
            metaData.freeBlocks = raf.readInt();
            metaData.name = raf.readUTF();
            metaData.date = raf.readUTF();
            metaData.time = raf.readUTF();
        }
        catch(Exception e){
            System.out.println("Error: could not load meta data");
            return false;
        }
        return true;
    }

    //Check the availability of the data block when writing data
    public static boolean isBlockAvailable(byte [] b){
        for(int i=0 ; i<b.length ; i++)
            if(b[i] != 0)
                return false;
        return true;
    }

    //open the specified disk of FS and save the opened stream to raf declared in the class to read write through it
    public static boolean openFS(String name){
        try{
            if(raf != null)
                raf.close();
            File f = new File(name);
            RandomAccessFile ram = new RandomAccessFile(f,"rw");
            openedFS = f;
            raf = ram;
            isOpen = true;
            if(!loadMeta()){
                return false;
            }
            System.out.println(name+" opened successfully");
            return true;
        }
        catch(Exception e){
            System.out.println("Unable to open File System(check the disk's name etc)...");
            return false;
        }

    }

    //list meta data of opened FS and all the Files with their meta inf
    public static void list(){
        System.out.println("FS Information: \n");
        System.out.println("Name: "+metaData.name);
        System.out.println("Created on: "+metaData.date +" "+ metaData.time);
        System.out.println("Size: "+metaData.totNumBlocks+" Blocks");
        System.out.println("Meta data size is "+metaData.metaBlocks+" Blocks");
        System.out.println("FNT size is "+metaData.numFNT+" Blocks");
        System.out.println("Total saved to data: "+metaData.numSavedBlocks+" Blocks");
        System.out.println("Number of blocks available to save data: "+metaData.freeBlocks+" Blocks");

        System.out.println("Files\n");
        try{
            int j = metaData.metaBlocks*128;
            String temp;
            int inode;
            raf.seek(j);
            for(int i=0 ; i<metaData.numFNT*2 ; i++){
                temp = raf.readUTF();
                if(!temp.equals("")){
                    inode = raf.readInt();
                    raf.seek(inode);
                    raf.readInt();
                    raf.readInt();
                    System.out.println("Name :\t"+temp+"\tSize (Bytes):\t"+raf.readInt()+"\tDate :\t"+raf.readUTF()+"\tTime :\t"+raf.readUTF()+"\tUser :\t"+raf.readUTF());
                }
                j += 64;
                raf.seek(j);
            }
        }
        catch(Exception e){
            System.out.println("Error");
        }

    }

    //remove the file starting from FNT and going further using the inode and next inode ptrs till all the blocks are set to null
    public static boolean remove(String name){
        try{
            int j = metaData.metaBlocks * 128;
            raf.seek(j);
            String temp="";
            int ptr=0;
            int dataP;
            for(int i=0 ; i<metaData.numFNT*2 ; i++){
                temp = raf.readUTF();
                if(temp.equals(name))
                    break;
                else{
                    j += 64;
                    raf.seek(j);
                }
            }

            if(temp.equals(name)){
                ptr = raf.readInt();
                raf.seek(j);
                raf.writeUTF(new String("".getBytes("UTF-8")));
                int tmp;
                byte [] tmpData = new byte[128];

                while(ptr!=0){
                    //points to inode
                    raf.seek(ptr);
                    tmp = ptr;
                    dataP = raf.readInt();
                    ptr = raf.readInt();
                    //write null bytes
                    raf.seek(dataP);
                    raf.write(tmpData);
                    raf.seek(tmp);
                    raf.write(tmpData);

                    //increment the size of free blocks by 2
                    metaData.freeBlocks += 2 ;
                    raf.seek(16);
                    raf.writeInt(metaData.freeBlocks);
                }
                return true;
            }
            else System.out.println("Error: file does not exist");
        }
        catch(Exception e){
            System.out.println("Error: file not copied");
        }
        return false;
    }

    //put the file into the disk, break the file into 128 byte parts and in the loop keep finding blocks to save inode and then data respectivly
    public static boolean put(String name){

        try{
            byte [] tmp = new byte[128];
            String nameA [] = name.split("(\\\\)|(/)");
            File f = new File(name);
            FileInputStream fileInStr = new FileInputStream(f);
            byte[] data = new byte[(int) f.length()];
            fileInStr.read(data);
            fileInStr.close();
            //checking if file name already present then donot add the file
            int z = metaData.metaBlocks * 128;
            String tempName;
            raf.seek(z);
            for(int i=0 ; i<metaData.numFNT*2 ; i++){
                tempName = raf.readUTF();
                if(tempName.equals(nameA[nameA.length-1])){
                    System.out.println("File with the same name already exist in the File System...");
                    return false;
                }
                z += 64;
                raf.seek(z);
            }

            if( ((int)(java.lang.Math.ceil((double)(data.length)/128))) <= (metaData.freeBlocks)/2 ){
                //Enter the filename in the FNT
                int j = metaData.metaBlocks * 128;
                raf.seek(j);
                //write condition so that j donot cross FNT area
                while(!(raf.readUTF().equals(""))){
                    j += 64;
                    raf.seek(j);
                }
                int FNTi = j;
                //Enter FNT entry after searching the place of inode
                j = (metaData.metaBlocks * 128) + (metaData.numFNT * 128);
                raf.seek(j);
                raf.read(tmp);
                while(!isBlockAvailable(tmp)){
                    j += 128;
                    raf.read(tmp);
                }
                int curI = j;
                int freeB;
                int nextI = 0;

                raf.seek(FNTi);
                raf.writeUTF(new String(nameA[nameA.length-1].getBytes("UTF-8")));
                raf.writeInt(curI);

                for( int i=0 ; i < ((int)(java.lang.Math.ceil((double)(data.length)/128))) ; i++ ){
                    //Find free block for entering data
                    j = curI + 128;
                    raf.seek(j);
                    raf.read(tmp);
                    while(!isBlockAvailable(tmp)){
                        j += 128;
                        raf.read(tmp);
                    }
                    freeB = j;

                    //Find free block for entering next inode
                    j += 128;
                    raf.seek(j);
                    raf.read(tmp);
                    while(!isBlockAvailable(tmp)){
                        j += 128;
                        raf.read(tmp);
                    }
                    nextI = j;

                    //Enter data into current INODE
                    if(i==0){
                        raf.seek(curI);
                        raf.writeInt(freeB);
                        raf.writeInt(nextI);
                        raf.writeInt(data.length);
                        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                        String [] t = df.format(new Date()).split(" ");
                        raf.writeUTF(new String(t[0].getBytes("UTF-8")));
                        raf.writeUTF(new String(t[1].getBytes("UTF-8")));
                        raf.writeUTF(new String(System.getProperty("user.name").getBytes("UTF-8")));
                    }
                    else{
                        raf.seek(curI);
                        raf.writeInt(freeB);
                        raf.writeInt(nextI);
                    }

                    //Write data into free block
                    raf.seek(freeB);
                    byte [] tmpW = new byte[128];
                    for(int x=0 ; x<128 ; x++)
                        tmpW[x] = 0;
                    int y=0;
                    for(int x=128*i ; x<(128*i)+128 && x<data.length ; x++  )
                        tmpW[y++] = data[x];
                    raf.write(tmpW);

                    //swap current inode with next inode
                    int tmpI = curI;
                    curI = nextI;
                    nextI = tmpI;

                    //decrease the free blocks size by 2
                    metaData.freeBlocks -= 2 ;
                    raf.seek(16);
                    raf.writeInt(metaData.freeBlocks);

                }
                if(nextI != 0){
                    raf.seek(nextI);
                    raf.readInt();
                    raf.writeInt(0);
                }
                return true;
            }
            else System.out.println("Not enough space in the disk. Please free some space.");
        }
        catch(Exception e){
            System.out.println("File not found or FNT becomes full");
        }

        return false;
    }

    //get the file from FS and save to the OS with name appended "out-" at start to represent the files comming from disk
    //start looking from FNT and then go further by following inodes and next inodes to write data into the file.
    public static boolean get(String name){
        try{
            int j = metaData.metaBlocks * 128;
            raf.seek(j);
            String temp="";
            int ptr=0;
            int dataP = 0;
            int size = 0;
            for(int i=0 ; i<metaData.numFNT*2 ; i++){
                temp = raf.readUTF();
                if(temp.equals(name))
                    break;
                else{
                    j += 64;
                    raf.seek(j);
                }
            }

            if(temp.equals(name)){
                File f = new File(name+ " disk_copy");
                FileOutputStream fos = new FileOutputStream(f);

                ptr = raf.readInt();
                raf.seek(ptr);
                dataP = raf.readInt();
                ptr = raf.readInt();
                size = raf.readInt();
                byte [] data = new byte[size];
                byte [] tmpData = new byte[128];
                raf.seek(dataP);
                raf.read(tmpData);
                fos.write(tmpData);
                while(ptr!=0){
                    raf.seek(ptr);
                    dataP = raf.readInt();
                    ptr = raf.readInt();

                    raf.seek(dataP);
                    raf.read(tmpData);
                    fos.write(tmpData);

                }
                fos.getChannel().truncate(size);
                fos.close();

                return true;
            }
            else System.out.println("Error: file does not exist");
        }
        catch(Exception e){
            System.out.println("Error: file does not exist.");
        }
        return false;
    }

    //change the ownership of the files of the current logged in user to the user specified in the name
    //simply go to first inode of each entry in FNT and check if its owner is current user then change it to name specified
    public static void user(String name){
        String curUser = System.getProperty("user.name");
        try{
            int count=0;
            int j = metaData.metaBlocks * 128;
            raf.seek(j);
            String temp="";
            String fUser;
            int ptr;
            for(int i=0 ; i<metaData.numFNT*2 ; i++){
                temp = raf.readUTF();
                if(!temp.equals("")){
                    ptr = raf.readInt();
                    raf.seek(ptr);
                    raf.readInt();
                    raf.readInt();
                    raf.readInt();
                    raf.readUTF();
                    raf.readUTF();
                    fUser = raf.readUTF();
                    if(fUser.equals(curUser)){
                        raf.seek(ptr);
                        raf.readInt();
                        raf.readInt();
                        raf.readInt();
                        raf.readUTF();
                        raf.readUTF();
                        raf.writeUTF(new String(name.getBytes("UTF-8")));
                        count++;
                    }
                }
                j += 64;
                raf.seek(j);
            }
            if(count > 0) System.out.println(count+" Files ownership changed from "+curUser+" to "+name);
            else System.out.println("No files exist ");
        }
        catch(Exception e){
            System.out.println("Error: ownership could not be changed");
        }
    }

    //create a link of the file, simply add another entry in FNT specified by arguments and save first inode of the file to it
    public static void link(String source, String newLink){
        try{
            int j = metaData.metaBlocks * 128;
            raf.seek(j);
            String temp="";
            int ptr = 0;
            for(int i=0 ; i<metaData.numFNT*2 ; i++){
                temp = raf.readUTF();
                if(temp.equals(source)){
                    ptr = raf.readInt();
                    break;
                }
                j += 64;
                raf.seek(j);
            }
            if(temp.equals(source)){
                j = metaData.metaBlocks * 128;
                raf.seek(j);
                //
                while(!(raf.readUTF().equals(""))){
                    j += 64;
                    raf.seek(j);
                }
                raf.seek(j);
                raf.writeUTF(new String(newLink.getBytes("UTF-8")));
                raf.writeInt(ptr);

                System.out.println("Link \""+newLink+"\" to the source file \""+source+"\" is created");
            }
            else System.out.println(source+ " does not exist");
        }
        catch(Exception e){
            System.out.println("Error creating a link (Your FNT may be full)");
        }
    }

    //remove the link if its hard link present other wise remove the complete file also
    public static void unlink(String name){
        try{
            int j = metaData.metaBlocks * 128;
            raf.seek(j);
            String temp="";
            int ptr = 0;
            int ptr2 = 0;
            int toRemove = 0;
            for(int i=0 ; i<metaData.numFNT*2 ; i++){
                temp = raf.readUTF();
                if(temp.equals(name)){
                    ptr = raf.readInt();
                    break;
                }
                j += 64;
                raf.seek(j);
            }
            toRemove = j;
            if(temp.equals(name)){
                j = metaData.metaBlocks * 128;
                raf.seek(j);
                for(int i=0 ; i<metaData.numFNT*2 ; i++){
                    temp = raf.readUTF();
                    ptr2 = raf.readInt();
                    if(ptr2 == ptr && !(temp.equals(name)))
                        break;
                    j += 64;
                    raf.seek(j);
                }

                if(ptr2 == ptr && !(temp.equals(name))){
                    raf.seek(toRemove);
                    raf.writeUTF(new String("".getBytes("UTF-8")));
                    System.out.println("Link \""+name+"\" to the file is removed...");
                }
                else{
                    System.out.println("No link has been found, so this file is entirely being removed...");
                    remove(name);
                }
            }
            else System.out.println(name+" does not exist");

        }
        catch(Exception e){
            System.out.println("Error");
        }
    }

    //main just create a command interpreter like command screen using switch statement
    //where userInput are entered and executed accordingly
    public static void main(String[] args) {

        String input = "";
        String name = "";
        Scanner s = new Scanner(System.in);

        System.out.println("\nEnter command (press q to exit): ");
        while(!(input.equals("q"))){

            System.out.printf(">>>  ");
            input = s.nextLine();
            String [] userInput = input.split(" ");
            switch(userInput[0]){
                case "createfs": // user types in createfs and then a number that is greater than 10
                    if(!isCreated){ // if true
                        int num;
                        try{
                            num = Integer.parseInt(userInput[1]);
                        }
                        catch(Exception e){ // error if the second word is not an integer
                            System.out.println("Integer was not entered");
                            break;
                        }
                        if(num >= 10){
                            isCreated = true;
                            fs.totNumBlocks = num; // create a file system disk of num blocks
                            System.out.println("File System has been created.");
                        }
                        else System.out.println("Must enter blocks greater than 10");
                    }
                    else System.out.println("File system already exists.");
                    break;

                case "formatfs":
                    if(userInput.length == 1){
                        if(isFormated)
                            System.out.println("File system is not yet saved");
                        else if(isCreated){
                            formatFS();
                            isFormated = true;
                            isCreated = false;
                        }
                        else System.out.println("Error: no file system to be formatted"); // user must create an FS first
                    }
                    else System.out.println("Enter valid number of arguments...");
                    break;

                case "savefs":
                    if(isFormated){
                        try{
                            name = input.replaceFirst("savefs ", "");
                            if( name.length() <= 56 && name.length() > 0 ){
                                saveFS(name);
                                isFormated = false;
                            }
                            else{
                                System.out.println("Not a valid name(length must be from 1 to 56)...");
                            }
                        }
                        catch(Exception e){
                            System.out.println("Error: invalid name");
                        }
                    }
                    else System.out.println("Error: file system must be formatted first.");
                    break;

                case "openfs":
                    try{
                        name = input.replaceFirst("openfs ", "");
                        if(openFS(name)) isOpen = true;
                        else System.out.println("File system not found");

                    }
                    catch(Exception e){
                        System.out.println("Error: Invalid name");
                    }
                    break;

                case "list":
                    if(isOpen) list();
                    else System.out.println("Error: File System must be opened first");
                    break;

                case "remove":
                    if(isOpen){
                        try{
                            name = input.replaceFirst("remove ", "");
                            if(remove(name)) System.out.println("File removed successfully");
                            else System.out.println("Error: File not removed");
                        }
                        catch(Exception e){
                            System.out.println("Error: file not copied");
                        }
                    }
                    else System.out.println("Error: File System must be opened first");
                    break;

                case "put":
                    if(isOpen){
                        try{
                            name = input.replaceFirst("put ", "");
                            if(put(name)) System.out.println("File has been copied to disk");
                            else System.out.println("Error: file not added");
                        }
                        catch(Exception e){
                            System.out.println("Error: Invalid name");
                        }
                    }
                    else System.out.println("Error: File System must be opened first");
                    break;

                case "get":
                    if(isOpen){
                        try{
                            name = input.replaceFirst("get ", "");
                            if(get(name))
                                System.out.println("File has been copied to OS");
                            else System.out.println("Error: file not copied");
                        }
                        catch(Exception e){
                            System.out.println("Error: Invalid name");
                        }
                    }
                    else System.out.println("Error: File System must be opened first");
                    break;

                case "user":
                    if(isOpen){
                        try{
                            name = input.replaceFirst("user ", "");
                            user(name);
                        }
                        catch(Exception e){
                            System.out.println("Error: Invalid name");
                        }
                    }
                    else System.out.println("Error: File System must be opened first");
                    break;

                case "link":
                    if(isOpen){
                        if(userInput.length == 3){
                            try{
                                link(userInput[1],userInput[2]);
                            }
                            catch(Exception e){
                                System.out.println("Error: Invalid names");
                            }
                        }
                        else System.out.println("Enter the correct number of arguments\n\tsource file1 file2...");
                    }
                    else System.out.println("Error: File System must be opened first");
                    break;

                case "unlink":
                    if(isOpen){
                        try{
                            unlink(userInput[1]);
                        }
                        catch(Exception e){
                            System.out.println("Error: Invalid name");
                        }
                    }
                    else System.out.println("Error: File System must be opened first");
                    break;

                case "q":
                    try{
                        raf.close();
                    }
                    catch(Exception e){}
                    break;

                default:
                    System.out.println("Error: Invalid command");
                    break;
            }
        }
    }
}
