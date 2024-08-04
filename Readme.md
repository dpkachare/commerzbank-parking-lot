### LotManager:

This is the main service class which implements the park() and unPark() API.
This class simple maps vehicle to its corresponding LotTracker. 
All the heavy work is delegated to LotTracker.


### LotTracker:

A simple data structure to encapsulate Lot and track number of available lots available.
Since we will have finite countable number of lots, the lots are numbered numerically starting with ID=0.

This data structure maintains an array of lots indexed by its ID.This ensures faster lookups and access to individual
lots using index in array.



