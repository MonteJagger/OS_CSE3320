// Hiumathy Lam
// 1001139731

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <pthread.h>
#define Rowsize 8792
pthread_t tid[2];

void* thread(void *arg);
int process();
int  bubblesort(int start, int size, int print);

struct earthquakes{
    char info[50];
    double lat;
    double longitude;
    double depth;
    double magnitude;
};

// only used for synchronizing stdout from overlap.
pthread_mutex_t mtx = PTHREAD_MUTEX_INITIALIZER;

// forward declare our thread proc
void *merge_sort_thread(void *pv);

struct earthquakes x[3173958];

int main (int argc, const char * argv[])
{
    FILE *f;
    char s[1000];
    f=fopen("all_month.csv", "r"); //opening file to read
    if(!f){
        printf("No File\n");
        return 1;
    }
    int count = 0;
    int array = 0;
    int in =0;
    char lati[20];
    int latid = 0;
    char lon[20];
    int longi = 0;
    char mag[20];
    int magni = 0;
    char dep[20];
    int dept = 0;
    while (fgets(s,1000,f)!=NULL) {             //getting all the names and locations and storing them in the struct which then goes into the array called "x"
        for(int i = 0; i < strlen(s); i++){
            if(s[i] == ','){
                count++;
            }
            if(count == 0){
                x[array].info[in] = s[i];
                in++;
            }
            if(count == 1){
                if(s[i] != ','){
                    lati[latid] = s[i];
                    latid++;
                }
            }
            if(count == 2){
                if(s[i] != ','){
                    lon[longi] = s[i];
                    longi++;
                }
            }
            if(count == 3){
                if(s[i] != ','){
                    dep[dept] = s[i];
                    dept++;
                }
            }
            if(count == 4){
                if(s[i] != ','){
                    mag[magni] = s[i];
                    magni++;
                }
            }
        }
        double lon1 = atof(lon);
        double lat1 = atof(lati);
        double mag1 = atof(mag);
        double dep1 = atof(dep);
        x[array].lat = lat1;
        x[array].longitude = lon1;
        x[array].depth = dep1;
        x[array].magnitude = mag1;
        array++;
        in = 0; latid = 0;longi = 0; count = 0, magni = 0, dept = 0;
    }
    
    int first = bubblesort(1,8792, 0);       //First bubblesort performed
    
    int choice = 0;
    int pr;
    int i = 0;
    int err;
    printf("Select 2, 4 or 8 number of process to run: \n");
    scanf("%d", &choice);
    while(choice > 8 || choice < 2){
        printf("Incorrect entry. Please select 2,4 or 8 number of process to run: ");
        scanf("%d", & choice);
    }
    if(choice == 2){                    //All the different number of processes that will be run
        pr = process(1, 100, 200);
    }
    if (choice == 4){
        pr = process(1, 100, 200);
        if(pr == 1)
            pr = process(100, 200, 300);
    }
    if (choice == 8){
        pr = process(1, 100, 200);
        if(pr == 1){
            pr = process(100, 200, 300);
            if(pr == 1){
                pr = process(200, 300, 400);
                if(pr == 1)
                    pr = process(300, 400, 500);
            }
        }
    }
    
    int i, par_impar = 0;
    v_initiate();
    
    printf("Vetor Ordenado\n3 5 6 4 1 2 9 7 8 10\n");
    
    do {
        
        swapped = 0;
        
        for(i = 0; i < Rowsize; i+=2)
            pthread_create(&thread[i], NULL, &bubble, i);
        for(i = 0; i < Rowsize; i+=2)
            pthread_join(thread[i], NULL);
        
        swapped = 0;
        
        for(i = 1; i < Rowsize; i+=2)
            pthread_create(&thread[i], NULL, &bubble, i);
        for(i = 1; i < Rowsize; i+=2)
            pthread_join(thread[i], NULL);
        
        //printf("swap par: %d - swap impar: %d\n", swapped_par, swapped_impar);
        
    } while(swapped == 1);
    
    printf("Vetor Ordenado\n");
    for(i = 0; i < Rowsize; i++)
        printf("%d ", a[i]);
    printf("\n");
    
    
    fclose(f);
    return 0;
}

int  bubblesort(int start, int size, int print)         //bubblesort function
{
    int i;
    int j;
    clock_t time;               //initializing the time variable to keep track of time
    struct earthquakes tmp;
    time=clock();
    for(i=start;i<size;i++)
    {
        for(j=start;j<size-1;j++)
        {
            if(x[j].magnitude > x[i].magnitude)
            {
                tmp=x[j];
                x[j]=x[i];
                x[i]=tmp;
            }
        }
    }
    if(print == 1){
        printf("\n");
        for(int y = start; y < size; y++)
            printf("%s \t%8.4f \t%8.4f \t%8.2f \t%8.2f\n", x[y].info, x[y].lat, x[y].longitude, x[y].depth, x[y].magnitude);
    }
    printf("\nTime taken to do bubble sort is %f seconds\n",((float)time)/CLOCKS_PER_SEC);
    return 0;
}

int process(int size1, int size2, int size3){
    int bs;
    pid_t pid = fork();                 //forking new process to run
    if(pid >= 0)
    {
        if(pid == 0)
        {
            bs = bubblesort(size1, size2, 1);
            return 1;
        }
        else
        {
            wait(NULL);
            bs = bubblesort(size2, size3, 1);
            return 0;
        }
    }
    else
    {
        printf("\nFork failed\n");
    }
    return 0;
}

void* thread(void *arg)
{
    unsigned long i = 0;
    pthread_t id = pthread_self();
    
    if(pthread_equal(id,tid[0]))
    {
        printf("\n First thread processing\n");
    }
    else
    {
        printf("\n Second thread processing\n");
    }
    
    for(i=0; i<(0xFFFFFFFF);i++);
    
    return NULL;
}

