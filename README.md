# Cache Simulator

This is a software simulation of a cache memory subsystem. 
This program uses an array of data to act as the memory, and an array of slot object to act as the cache.

 * Direct-Mapped, Write-Back Cache
 * Block size: 16 bytes (16 addresses)
 * Number of slots: 16
 * Block offset: 4 bits = 1 byte

///////////

The 2K memory is initialized with values of x0 to xFF, starting over at x0 when xFF is reached. The cache is initalized to all zeros. This program asks the user if the user wants to read, write, or display the cache.

1. Read - The program asks the user to enter the address of the memory location the user wants to retrieve the data for. The program will retrieve the data, either from cache or from memory, and outputs it to the console. The program will also indicate whether the address read was in the cache (cache hit) or needed to be fetched from memory (cache miss).

2. Write - The program asks the user to enter the address of the memory location the suer wants to write new data to, and the data the user wishes to write. The program will store the updated data into the cache, and then output the address and the updated data. The program will also indicate if the address was already in the cache (cache hit), or if it needed to be fetched from memory (cache miss).

3. Display - The program will print out the cache in a neat, readable format; displaying all attributes of the cache model.

If any data in the cache needs to be overwritten with new data at any point, the program will check if the current data in the cache has changed from memory (using the dirty bit). If so, the new data gets written to memory.
