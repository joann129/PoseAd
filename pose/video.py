# From Python
# It requires OpenCV installed for Python
# python video.pys
#include/openpose/flag.h
import sys
import cv2
import os
from sys import platform
import argparse
import numpy as np

try:
    # Import Openpose (Windows/Ubuntu/OSX)
    dir_path = os.path.dirname(os.path.realpath(__file__))
    try:
        # Windows Import
        if platform == "win32":
            # Change these variables to point to the correct folder (Release/x64 etc.)
            sys.path.append(dir_path + '/Release');
            os.environ['PATH']  = os.environ['PATH'] + ';' + dir_path + '/Release;' +  dir_path + '/bin;'
            import pyopenpose as op
        else:
            # Change these variables to point to the correct folder (Release/x64 etc.)
            sys.path.append('../../python');
            # If you run `make install` (default path is `/usr/local/python` for Ubuntu), you can also access the OpenPose/python module from there. This will install OpenPose and the python library at your desired installation path. Ensure that this is in your python path in order to use it.
            # sys.path.append('/usr/local/python')
            from openpose import pyopenpose as op
    except ImportError as e:
        print('Error: OpenPose library could not be found. Did you enable `BUILD_PYTHON` in CMake and have this Python script in the right folder?')
        raise e

    # Flags
    parser = argparse.ArgumentParser()
    parser.add_argument("--image_path", default="../../../examples/media/COCO_val2014_000000000192.jpg", help="Process an image. Read all standard formats (jpg, png, bmp, etc.).")
    args = parser.parse_known_args()

    # Custom Params (refer to include/openpose/flags.hpp for more parameters)
    params = dict()
    params["model_folder"] = "models/"
    params["write_json"] = "jsonOutput"
    params["net_resolution"] = "160x144"

    # Add others in path?
    for i in range(0, len(args[1])):
        curr_item = args[1][i]
        if i != len(args[1])-1: next_item = args[1][i+1]
        else: next_item = "1"
        if "--" in curr_item and "--" in next_item:
            key = curr_item.replace('-','')
            if key not in params:  params[key] = "1"
        elif "--" in curr_item and "--" not in next_item:
            key = curr_item.replace('-','')
            if key not in params: params[key] = next_item

    # Construct it from system arguments
    # op.init_argv(args[1])
    # oppython = op.OpenposePython()

    # Starting OpenPose
    
    #opWrapper = op.WrapperPython(op.ThreadManagerMode.Synchronous)
    opWrapper = op.WrapperPython()
    opWrapper.configure(params)
    opWrapper.start()
    #opWrapper.execute()

    datum = op.Datum()
    cap = cv2.VideoCapture("video/03cut.mp4")
    
    while True:
        ret,frame = cap.read()
        if ret == False:
            print("erro")
        img = frame
        datum.cvInputData = img
        
        opWrapper.emplaceAndPop([datum])
        print(str(datum.poseKeypoints[0][8]))
        #cv2.resizeWindow("frame", 160, 90);
        cv2.imshow("frame", datum.cvOutputData)
        #cv2.imshow("frame1", frame)
        cv2.waitKey(1)

except Exception as e:
    print(e)
    sys.exit(-1)
