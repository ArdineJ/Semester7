import cv2
from cv2 import imread, imshow, waitKey
import matplotlib.pyplot as plt 

imagepath= '/orange.jpg'
img = imread(imagepath)
print('Datatype:', img.dtype, '\nDimensions:', img.shape)
print(img[0, 0])
plt.imshow(img) # use plt alias
plt.title('Displaying image using Matplotlib') # use plt alias
plt.show()

imgcv2 = cv2.imread(imagepath)
cv2.imshow('Displaying image using OpenCV', imgcv2)
cv2.waitKey(0)
cv2.destroyAllWindows ()