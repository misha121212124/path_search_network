# path_search_network
This is my first neural network. Task: find the direction that corresponds to the shortest path to the destination for a given point on the map

The program automatically generates a map of a given size (along with obstacles). The destination point is necessarily generated on the map. The network then begins training to minimize errors in determining direction. To learn, you need to adjust the map (right and left mouse buttons add or remove an obstacle). For the best learning (on the first map) it is necessary that the map has a close number of points with different optimal directions.
To teach the universal network, you can change the map while the program is running (try additional mouse buttons, if you have them), or return to the previous ones.
The program is not optimized. All calculations take place in one stream without the GPU.
