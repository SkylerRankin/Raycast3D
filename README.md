# Transforming 2D Maps into 3D Environments with Ray Casting

In the early days of computer hardware, game developers had to use clever techniques of creating perspective without much memory or computing power. Inspired by the MS-DOS game [Wolfenstein 3D](https://en.wikipedia.org/wiki/Wolfenstein_3D), this single Java file uses one such technique: ray casting. Ray casting was first presented for computer graphics in 1982 by Scott Roth.

My implementation uses three properties to simulate depth: the relative height of the walls, the darkness of the color of the walls, and the color gradients on the floor and ceiling.

The algorithm begins with a 2D map of the area. This is as simple as a matrix of integers denoting empty space as `0` and different colored walls `1, 2, and 3`.

![Alt Text](https://github.com/SkylerRankin/Raycast3D/blob/master/res/map.PNG)

We then split the field of view into columns of equal width. Here we use a field of view of `90 degrees`, and `200` columns of width `5` pixels. Then, at each frame, for each of these columns, we calculate the angle between looking straight ahead and looking directly at that column. This is done on the 2D map of the environment. We can then follow that direction until the first non-zero cell is hit, and record that distance. With the distance for each column recorded, we can set the vertical height of the wall in that column to be larger if the distance was small, and smaller if the distance was large.

![alt text](https://github.com/SkylerRankin/Raycast3D/blob/master/res/demo.gif)

In this example, we can see the blue vertical bars representing 2 of the 200 columns that make up the image. The corner of the green wall is pretty close in the 2D map, so it gets a tall green box. The purple wall in the distance is quite far in the 2D map, so its cyan box is much shorter.

![Alt Text](https://github.com/SkylerRankin/Raycast3D/blob/master/res/diagram.jpg)

## Usage
Download and double click on the jar file, or run from a command line with:
```sh
> java -jar .\Raycast3D.jar
```