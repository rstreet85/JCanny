#JCanny
A pure Java implementation of John Canny's 1986 edge detector, including a Gaussian filter. The algorithm accepts an image, converts it to grayscale, blurs it with a Gaussian filter, and then detects the edges within it. It does so by finding the 'edge' magnitude of each pixel with a bi-directional Sobel operator, then disregarding all 'weak' edge pixels unless they are directly adjacent to a 'strong' edge pixel (**hysteresis**). The Canny is elegant - despite it's age and simplicity, it is still **the** standard for edge detection, and readily lends itself to optimization. This program is intended to provide quick method of detecting edges and study the Canny method, as well as getting quick feedback while tweaking parameters for specific projects.

##Usage
**Command-line arguments:** *-fileName* *-outputFileExtension*

##To-Do
- Allow user to (optionally) set the paramters for Gaussian filter & hysteresis for optimization purposes
- Add Pratt figure of merit functionality, so that user can designate what edges they want, and determine how closely the output from parameters match.
- Explore additional preprocessing methods, such as white balance.

###Support This Project
:+1: If you wish to help fund further development of this project, feel free to donate to 1fyg8vmfoD2WkhSBK7BFRNgYEzbe1Lpyf :tea::coffee::beer:
