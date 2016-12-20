/*  
    Hiumathy Lam
    2168-CSE-3320-001
    Assignment #1
*/

#include <sys/types.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <dirent.h>
#include <string.h>
#include <time.h>

int k;

int main(void) {
    DIR * d;
    struct dirent * de;
    int i, j, c, numOfFiles, numOfDir, count, a=0;
    char s[256], cmd[256];
    int userInt;
    time_t t;
    struct tm* info;
    char buffer[80], temp[90], gcc[256] = "gcc -o run ";
    
    while (1) {
        // so the menu does not print twice
        if (a > 0)
            getchar();
        
        // CURRENT DIRECTORY
        getcwd(s, 200); // get the pathname of the current working directory
        printf( "\nCurrent Directory: %s \n", s); // prints the pathname
        
        // TIME
        time(&t); // setting time
        info = localtime( &t );
        strftime(buffer, 80, "%d %B %Y, %I:%M %p", info);
        printf( "It is now: %s\n\n", buffer); // printing time
        
        // FILES
        d = opendir( "." );
        
        // gets the size of the files
        c = 0;
        while ((de = readdir(d))){
            if (((de->d_type) & DT_REG)){
                c++;
                strcpy(temp, de->d_name);
            }
        }
        //printf("There are %d files: \n", c);
        closedir( d );
        
        numOfFiles = c;
        char file[numOfFiles][1024];
        
        // copies the files to an string array
        d = opendir( "." );
        c = 0;
        printf("Files:");
        while ((de = readdir(d)))
        {
            if (((de->d_type) & DT_REG))
            {
                strcpy(file[c], de->d_name);
                c++;
            }
        }
        closedir(d);
        
        // prints the files
        if ((k+5) > numOfFiles && (k+5) < 5)
            for (i=0; i<numOfFiles; i++)
                printf("\t\t%d.   %s\n", i, file[i]);
        else if ((k+5) > numOfFiles && (k+5) > 5)
            for (i=k; i<numOfFiles; i++)
                printf("\t\t%d.   %s\n", i, file[i]);
        else
            for (i=k; i<k+5; i++)
                printf("\t\t%d.   %s\n", i, file[i]);
        
        printf( "\n" );

        
        // DIRECTORIES
        d = opendir( "." ); // opens a directory stream corresponding to the directory name, and returns a pointer to the directory stream.
        c = 0;
    
        // counts the number of the directory
        while ((de = readdir(d))){
            if ((de->d_type) & DT_DIR){
                c++;
                strcpy(temp, de->d_name);
            }
        }
        closedir(d);
        //printf("Number of directories %d\n", c);
        
        d = opendir(".");
        numOfDir = c; // number of directories
        char directory[numOfDir][1024]; // array of directories
        printf("Directories:\n");
        
        // copies the directory to an array
        c = 0;
        while ((de = readdir(d))){
            if ((de->d_type) & DT_DIR){
                strcpy(directory[c], de->d_name);
                c++;
            }
        }
        closedir( d );
        
        // print directories
        for (i=0; i<numOfDir; i++){
            if (strcmp(directory[i], "..") == 0)
                printf("\t\t%d.   PREVIOUS Directory\n", i);
            else
                printf("\t\t%d.   %s\n", i, directory[i]);
        }
        
        printf( "\n" );
        
        // MENU ------------------------------------------------------------
        
        printf("Operation:\n");
        printf("\t\te   Edit \n");
        printf("\t\tr   Run  \n");
        printf("\t\tc   Change Directory \n");
        printf("\t\tq   Quit \n");
        printf("\t\tn   Next Files \n");
        printf("\t\tp   Previous Files \n");
        
        printf("\nType the letter from the Operation menu: ");
        
        a = getchar( );
    
        switch (a) {
            case 'q': exit(0); /* quit */
            case 'e': printf( "Edit what?: (Enter integer) " );
                count = 0;
                scanf( "%d", &userInt);
                for (i=0; i<numOfFiles; i++)
                    if (userInt == i){
                        strcpy( cmd, "pico ");
                        strcat( cmd, file[i]);
                        system( cmd );
                        count = 1;
                        break;
                    }
                
                if ( count == 0)
                    printf("DOES NOT EXIST\n");
                
                break;
            case 'r': printf( "Run what?: (Enter integer) " );
                
                count = 0;
                scanf( "%d", &userInt);
                printf("\n");
                for (i=0; i<numOfFiles; i++)
                    
                    if (userInt == i){
                        // executes .c files
                        for (j=0; j<1024; j++)
                            if ( file[i][j] == '.' && file[i][j+1] == 'c') {
                                strcat(gcc, file[i]);
                                system(gcc);
                                strcpy( cmd, "./");
                                strcat( cmd, "run");
                                system( cmd );
                                printf("\n\n");
                                count = 1;
                                break; // breaks the inner loop
                            }
                        
                        if (count == 1)
                            break; // breaks the outer loop
                        
                        // runs executable
                        strcpy( cmd, "./");
                        strcat( cmd, file[i]);
                        system( cmd );
                        count = 2;
                        printf("\n\n");
                        break;
                    }
                
                if ( count == 0)
                    printf("DOES NOT EXIST\n");
                break;
            case 'c': printf( "Change To?: (Enter integer) " );
                count = 0;
                scanf( "%d", &userInt); // user inters an integer
                for (i=0; i<numOfDir; i++)
                    if (userInt == i){
                        chdir( directory[i] );
                        k=0;
                        count = 1;
                        break;
                    }
                
                if ( count == 0)
                    printf("DOES NOT EXIST\n");

                break;
            case 'n':
                if ((k+5) > numOfFiles)
                    printf("NO MORE FILES\n");
                else
                    k += 5;
                break;
            case 'p':
                if ((k-5) < 0)
                    printf("Cannot go to back\n");
                else
                    k -= 5;
                break;
            default: printf("Invalid input");
        }
        a++;
    }
}