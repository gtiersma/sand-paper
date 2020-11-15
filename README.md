<p align="center">
    <img src="/presentation/logo.png" height="192" width="256">
    <img src="/presentation/title.png" height="128" width="480">
</p>

#

Sand Paper is a 3D modeling software solution for graphic designers that have limited knowledge with 3D modeling. Users create images that Sand Paper uses to construct three-dimensional scenery. Users then create rendered images of the scenery to further use in their image-editing projects.

Sand Paper is designed to function as a companion application that is used in collaboration with an image editor of the user's choice. The 3D object(s) that Sand Paper creates are constructed entirely with 2D images.

<p align="center">
    <img src="/presentation/screen.png">
</p>

To understand how to use Sand Paper, please take a look at the following beginner-friendly tutorial:

https://github.com/gtiersma/sand-paper/blob/master/SandPaper/tutorials/beginner.pdf

This tutorial is also accessible from within the program. It can be found on the help menu on the menu bar.

# Technologies

* Java v1.8.0_141
* JavaFX v8.0.171
* Programmed in Netbeans IDE v8.2

# Features

* Import an image as a displacement map to define the object's shape. Sand Paper uses colored displacements maps to position vertices in three dimensions.
* Bump and specular map support
* Create populations (groups of 3D objects) from images that are used to define their shape, position and appearance
* Create and easily position lights
* An easily adjustable camera
* Save renders of the 3D scenery as PNG images

# Change Log

* v0.5 (6/23/2019)
    * beta release
  
* v0.51 (7/21/2019)
    * multi-threading functionality for vertex calculations
    * minor bug fixes and improvements

* v0.6 (12/16/2019)
    * camera tab implemented, allowing the user to control the camera
    * light tab implemented, allowing the user to control the lighting
    * minor bug fixes and improvements

* v0.7 (6/10/2020)
    * population tab implemented, allowing the user to create and customize populations
    * added a help box, however it is not yet functional
    * visual redesign
    * a beginner tutorial is available
    * a progress bar dialog appears during tasks that may take more than a second
    * all of the controls have their input validated
    * many major and minor bug fixes
  
# To-Do List

* Create an intermediate and advanced tutorial
* Create a complete manual
* Finish shortcuts, hot-keys, keyboard controls, tab-order on controls
* Create a project file format, allowing users to save and load all of the values set in the controls
* Ability to remove imported textures
* Have Sand Paper occasionally check imported textures for changes, so that the user will not have to re-import textures every time they make a change
* Fix the delay that can occur after the progress dialog closes and before the stage becomes active
* Replace a few of the sliders with spinners

# Copyright

Sand Paper 3D Modeling Solution (formerly tentatively titled, "Project Scaper")
Copyright (C) 2019-2020 George Tiersma

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.