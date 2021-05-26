import sys
import cv2
import os
from sys import platform
import argparse
import numpy as np
import math
import pylint
from datetime import datetime
import subprocess


try:
    # Import Openpose (Windows/Ubuntu/OSX)
    dir_path = os.path.dirname(os.path.realpath(__file__))
    try:
        # Windows Import
        if platform == "win32":
            # Change these variables to point to the correct folder (Release/x64 etc.)
            sys.path.append(dir_path + '/Release')
            os.environ['PATH'] = os.environ['PATH'] + ';' + \
                dir_path + '/Release;' + dir_path + '/bin;'
            import pyopenpose as op
        else:
            # Change these variables to point to the correct folder (Release/x64 etc.)
            sys.path.append('../../python')
            # If you run `make install` (default path is `/usr/local/python` for Ubuntu), you can also access the OpenPose/python module from there. This will install OpenPose and the python library at your desired installation path. Ensure that this is in your python path in order to use it.
            # sys.path.append('/usr/local/python')
            from openpose import pyopenpose as op
    except ImportError as e:
        print('Error: OpenPose library could not be found. Did you enable `BUILD_PYTHON` in CMake and have this Python script in the right folder?')
        raise e

    # Flags
    parser = argparse.ArgumentParser()
    parser.add_argument("--image_path", default="../../../examples/media/COCO_val2014_000000000192.jpg",
                        help="Process an image. Read all standard formats (jpg, png, bmp, etc.).")
    args = parser.parse_known_args()

    # Custom Params (refer to include/openpose/flags.hpp for more parameters)
    params = dict()
    params["model_folder"] = "models/"
    params["write_json"] = "jsonOutput"
    #params["net_resolution"] = "160x144"
    #params["net_resolution"] = "256x144"
    params["net_resolution"] = "256x320"

    opWrapper = op.WrapperPython()
    opWrapper.configure(params)
    opWrapper.start()

    datum = op.Datum()
    cap = cv2.VideoCapture(0)
    count = 0;

    now = datetime.now()
    current_time = now.strftime("%H:%M:%S")
    print("Current Time =", current_time)

    # p=subprocess.Popen("python runvideo.py", shell=True)  

    while True:
        ret, frame = cap.read()
        if ret == False:
            print("error")

        img = frame
        datum.cvInputData = img

        opWrapper.emplaceAndPop([datum])
        
        openframe = datum.cvOutputData
        count += 1
        cv2.putText(openframe, str(count), (30,30), cv2.FONT_HERSHEY_SIMPLEX, 1, (255,0,0), 3, cv2.LINE_AA)
        cv2.imshow("frame", openframe)

        if cv2.waitKey(1) & 0xFF == ord('q'):
            now = datetime.now()
            current_time = now.strftime("%H:%M:%S")
            print("Current Time =", current_time)

            print(count)
            cv2.destroyAllWindows()
            break

    # Release
    cap.release()
    cv2.destroyAllWindows()

except Exception as e:
    print(e)

    now = datetime.now()
    current_time = now.strftime("%H:%M:%S")
    print("Current Time =", current_time)

    print(count)

    sys.exit(-1)