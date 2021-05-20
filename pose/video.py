# From Python
# It requires OpenCV installed for Python
# python video.py
# include/openpose/flag.h
import sys
import cv2
import os
from sys import platform
import argparse
import numpy as np
import math


def calAngle(p1, p2, p3):
    if p1[0] == 0 or p1[1] == 0 or p2[0] == 0 or p2[1] == 0 or p3[0] == 0 or p3[1] == 0:
        
        return -1

    vector1 = [p2[0]-p1[0], p2[1]-p1[1]]
    vector2 = [p3[0]-p2[0], p3[1]-p2[1]]
    angle = math.atan2(vector2[1], vector2[0]) - \
        math.atan2(vector1[1], vector1[0])
    angle = angle/math.pi*180  # change arc to degree
    if angle < 0:
        angle = angle + 360
    return angle


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

    # Add others in path?
    for i in range(0, len(args[1])):
        curr_item = args[1][i]
        if i != len(args[1])-1:
            next_item = args[1][i+1]
        else:
            next_item = "1"
        if "--" in curr_item and "--" in next_item:
            key = curr_item.replace('-', '')
            if key not in params:
                params[key] = "1"
        elif "--" in curr_item and "--" not in next_item:
            key = curr_item.replace('-', '')
            if key not in params:
                params[key] = next_item

    # Construct it from system arguments
    # op.init_argv(args[1])
    # oppython = op.OpenposePython()

    # Starting OpenPose

    #opWrapper = op.WrapperPython(op.ThreadManagerMode.Synchronous)
    opWrapper = op.WrapperPython()
    opWrapper.configure(params)
    opWrapper.start()
    # opWrapper.execute()

    datum = op.Datum()
    cap = cv2.VideoCapture("video/twice_L1.mp4")
    # cap = cv2.VideoCapture(0)
    out = None
    fw = open("point/point.txt", 'w')
    count = 0
    mid=0
    flag=0

    while True:
        ret, frame = cap.read()
        if ret == False:
            print("error")
        count += 1
        img = frame
        datum.cvInputData = img

        opWrapper.emplaceAndPop([datum])

        #if datum.poseKeypoints.any() == False : print("is empty")
        # print(datum.poseKeypoints.dtype)
        # print(str(datum.poseKeypoints[0][0]))
        #cv2.resizeWindow("frame", 160, 90);

        x = datum.poseKeypoints[mid][0][0]
        y = datum.poseKeypoints[mid][0][1]
        people = len(datum.poseKeypoints)
        openframe = datum.cvOutputData
        cv2.putText(openframe, str(count), (30,30), cv2.FONT_HERSHEY_SIMPLEX, 1, (255,0,0), 3, cv2.LINE_AA)
        cv2.putText(openframe, str(people), (60,60), cv2.FONT_HERSHEY_SIMPLEX, 1, (255,0,0), 3, cv2.LINE_AA)
        cv2.imshow("frame", openframe)
        if(str(datum.poseKeypoints) == "2.0" or str(datum.poseKeypoints) == "0.0"):
            continue

        if people > 1:
            for i in range(0,people):
                for j in range(0,18):
                    if(datum.poseKeypoints[i][j][0]==0.0 or datum.poseKeypoints[i][j][1]==0.0):
                        flag=1
                        break;
                if(flag==0):
                    mid = i
                    break
                else:
                    flag=0


        fw.write(str(count))
        fw.write("\n")
        # just point
        # for i in range (25):
        #     fw.write( str( round( datum.poseKeypoints[mid][i][0], 5 ) ) )
        #     fw.write(" ")
        #     fw.write( str( round( datum.poseKeypoints[mid][i][1], 5 ) ) )
        #     fw.write("\n")

        # write 28 times
        # fw.write( str( calAngle( datum.poseKeypoints[mid][16], datum.poseKeypoints[mid][0], datum.poseKeypoints[mid][15] ) ) )
        # fw.write("\n")
        # fw.write( str( calAngle( datum.poseKeypoints[mid][15], datum.poseKeypoints[mid][0], datum.poseKeypoints[mid][1] ) ) )
        # fw.write("\n")
        # fw.write( str( calAngle( datum.poseKeypoints[mid][1], datum.poseKeypoints[mid][0], datum.poseK    eypoints[mid][16] ) ) )
        # fw.write("\n")

        fw.write(str(calAngle(datum.poseKeypoints[mid][0], datum.poseKeypoints[mid][1], datum.poseKeypoints[mid][2])))
        fw.write("\n")
        fw.write(str(calAngle(datum.poseKeypoints[mid][2], datum.poseKeypoints[mid][1], datum.poseKeypoints[mid][8])))
        fw.write("\n")
        fw.write(str(calAngle(datum.poseKeypoints[mid][8], datum.poseKeypoints[mid][1], datum.poseKeypoints[mid][5])))
        fw.write("\n")
        fw.write(str(calAngle(datum.poseKeypoints[mid][5], datum.poseKeypoints[mid][1], datum.poseKeypoints[mid][0])))
        fw.write("\n")
        fw.write(str(calAngle(datum.poseKeypoints[mid][1], datum.poseKeypoints[mid][2], datum.poseKeypoints[mid][3])))
        fw.write("\n")
        fw.write(str(calAngle(datum.poseKeypoints[mid][2], datum.poseKeypoints[mid][3], datum.poseKeypoints[mid][4])))
        fw.write("\n")
        fw.write(str(calAngle(datum.poseKeypoints[mid][1], datum.poseKeypoints[mid][5], datum.poseKeypoints[mid][6])))
        fw.write("\n")
        fw.write(str(calAngle(datum.poseKeypoints[mid][7], datum.poseKeypoints[mid][6], datum.poseKeypoints[mid][5])))
        fw.write("\n")
        fw.write(str(calAngle(datum.poseKeypoints[mid][1], datum.poseKeypoints[mid][8], datum.poseKeypoints[mid][9])))
        fw.write("\n")
        fw.write(str(calAngle(datum.poseKeypoints[mid][12], datum.poseKeypoints[mid][8], datum.poseKeypoints[mid][1])))
        fw.write("\n")
        fw.write(str(calAngle(datum.poseKeypoints[mid][9], datum.poseKeypoints[mid][8], datum.poseKeypoints[mid][12])))
        fw.write("\n")
        fw.write(str(calAngle(datum.poseKeypoints[mid][10], datum.poseKeypoints[mid][9], datum.poseKeypoints[mid][8])))
        fw.write("\n")
        fw.write(str(calAngle(datum.poseKeypoints[mid][9], datum.poseKeypoints[mid][10], datum.poseKeypoints[mid][11])))
        fw.write("\n")
        # fw.write( str( calAngle( datum.poseKeypoints[mid][10], datum.poseKeypoints[mid][11], datum.poseKeypoints[mid][22] ) ) )
        # fw.write("\n")
        # fw.write( str( calAngle( datum.poseKeypoints[mid][22], datum.poseKeypoints[mid][11], datum.poseKeypoints[mid][24] ) ) )
        # fw.write("\n")
        # fw.write( str( calAngle( datum.poseKeypoints[mid][24], datum.poseKeypoints[mid][11], datum.poseKeypoints[mid][10] ) ) )
        # fw.write("\n")
        fw.write(str(calAngle(datum.poseKeypoints[mid][8], datum.poseKeypoints[mid][12], datum.poseKeypoints[mid][13])))
        fw.write("\n")
        fw.write(str(calAngle(datum.poseKeypoints[mid][14], datum.poseKeypoints[mid][13], datum.poseKeypoints[mid][12])))
        fw.write("\n")
        # fw.write( str( calAngle( datum.poseKeypoints[mid][19], datum.poseKeypoints[mid][14], datum.poseKeypoints[mid][13] ) ) )
        # fw.write("\n")
        # fw.write( str( calAngle( datum.poseKeypoints[mid][21], datum.poseKeypoints[mid][14], datum.poseKeypoints[mid][19] ) ) )
        # fw.write("\n")
        # fw.write( str( calAngle( datum.poseKeypoints[mid][13], datum.poseKeypoints[mid][14], datum.poseKeypoints[mid][21] ) ) )
        # fw.write("\n")

        # fw.write( str( calAngle( datum.poseKeypoints[mid][17], datum.poseKeypoints[mid][15], datum.poseKeypoints[mid][0] ) ) )
        # fw.write("\n")
        # fw.write( str( calAngle( datum.poseKeypoints[mid][0], datum.poseKeypoints[mid][16], datum.poseKeypoints[mid][18] ) ) )
        # fw.write("\n")
        # fw.write( str( calAngle( datum.poseKeypoints[mid][20], datum.poseKeypoints[mid][19], datum.poseKeypoints[mid][14] ) ) )
        # fw.write("\n")
        # fw.write( str( calAngle( datum.poseKeypoints[mid][11], datum.poseKeypoints[mid][22], datum.poseKeypoints[mid][23] ) ) )
        # fw.write("\n")

        if out is None:
            fourcc = cv2.VideoWriter_fourcc(*"MJPG")
            out = cv2.VideoWriter('output.avi', fourcc, 30, (frame.shape[1], frame.shape[0]))
            # print(frame.shape[1], frame.shape[0]) #640*360

        out.write(openframe)

        #cv2.imshow("frame1", frame)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            fw.close()
            cap.release()
            out.release()
            cv2.destroyAllWindows()
            break

    # Release
    fw.close()
    cap.release()
    out.release()
    cv2.destroyAllWindows()

except Exception as e:
    print(e)
    sys.exit(-1)
