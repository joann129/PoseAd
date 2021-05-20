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
import pylint
from datetime import datetime

def calAngle(p1, p2, p3):
    if p1[0]==0 or p1[1]==0 or p2[0]==0 or p2[1]==0 or p3[0]==0 or p3[1]==0:
        return -1

    vector1 = [p2[0]-p1[0], p2[1]-p1[1]]
    vector2 = [p3[0]-p2[0], p3[1]-p2[1]]
    angle = math.atan2(vector2[1], vector2[0]) - math.atan2(vector1[1], vector1[0])
    angle = angle/math.pi*180	# change arc to degree
    if angle < 0:
        angle= angle + 360
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
    cap = cv2.VideoCapture(0)
    # cap = cv2.VideoCapture(0)
    fr = open("point/pointTwticeL1.txt",'r')
    count = 0
    flag=0
    arr = [0]*15
    mid=0
    bad=0
    out=None

    while True:
        ret, frame = cap.read() #640x480
        if ret == False:
            print("error")
        # now = datetime.now()
        # current_time = now.strftime("%H:%M:%S")
        # print("Current Time =", current_time)

        count += 1
        inp = fr.readline().replace('\n','')
        img = frame
        datum.cvInputData = img

        opWrapper.emplaceAndPop([datum])

        #if datum.poseKeypoints.any() == False : print("is empty")
        # print(datum.poseKeypoints.dtype)
        # print(str(datum.poseKeypoints[0][0]))
        #cv2.resizeWindow("frame", 160, 90);
        
        
        people = len(datum.poseKeypoints)
        openframe = datum.cvOutputData
        
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
        
        arr[0] = calAngle(datum.poseKeypoints[mid][0], datum.poseKeypoints[mid][1], datum.poseKeypoints[mid][2])
        arr[1] = calAngle(datum.poseKeypoints[mid][2], datum.poseKeypoints[mid][1], datum.poseKeypoints[mid][8])
        arr[2] = calAngle(datum.poseKeypoints[mid][8], datum.poseKeypoints[mid][1], datum.poseKeypoints[mid][5])
        arr[3] = calAngle(datum.poseKeypoints[mid][5], datum.poseKeypoints[mid][1], datum.poseKeypoints[mid][0])

        arr[4] = calAngle(datum.poseKeypoints[mid][1], datum.poseKeypoints[mid][2], datum.poseKeypoints[mid][3])
        arr[5] = calAngle(datum.poseKeypoints[mid][2], datum.poseKeypoints[mid][3], datum.poseKeypoints[mid][4])

        arr[6] = calAngle(datum.poseKeypoints[mid][1], datum.poseKeypoints[mid][5], datum.poseKeypoints[mid][6])
        arr[7] = calAngle(datum.poseKeypoints[mid][7], datum.poseKeypoints[mid][6], datum.poseKeypoints[mid][5])

        arr[8] = calAngle(datum.poseKeypoints[mid][1], datum.poseKeypoints[mid][8], datum.poseKeypoints[mid][9])
        arr[9] = calAngle(datum.poseKeypoints[mid][12], datum.poseKeypoints[mid][8], datum.poseKeypoints[mid][1])
        arr[10] = calAngle(datum.poseKeypoints[mid][9], datum.poseKeypoints[mid][8], datum.poseKeypoints[mid][12])

        arr[11] = calAngle(datum.poseKeypoints[mid][10], datum.poseKeypoints[mid][9], datum.poseKeypoints[mid][8])
        arr[12] = calAngle(datum.poseKeypoints[mid][9], datum.poseKeypoints[mid][10], datum.poseKeypoints[mid][11])

        arr[13] = calAngle(datum.poseKeypoints[mid][8], datum.poseKeypoints[mid][12], datum.poseKeypoints[mid][13])
        arr[14] = calAngle(datum.poseKeypoints[mid][14], datum.poseKeypoints[mid][13], datum.poseKeypoints[mid][12])

        for i in range(0,15):
            tmp = (float)(fr.readline().replace('\n',''))
            if(tmp<0 or arr[i]<0):
                continue;
            if i<4:
                keyMid=1
            elif i<6:
                keyMid=i-2
            elif i<8:
                keyMid=i-1
            elif i<11:
                keyMid=8
            elif i<13:
                keyMid=i-2
            else:
                keyMid=i-1
            if tmp >= 315 and arr[i] < 45:
                if arr[i]+360-tmp > 45:
                    bad+=1
                    if bad > 728:
                        cv2.putText(openframe, "bad", (datum.poseKeypoints[mid][keyMid][0],datum.poseKeypoints[mid][keyMid][1]), cv2.FONT_HERSHEY_SIMPLEX, 1, (255,0,0), 3, cv2.LINE_AA)
                        print(count,i,arr[i]+360-tmp)
            elif arr[i] >= 315 and tmp < 45:
                if tmp+360-arr[i] > 45:
                    bad+=1
                    if bad > 728:
                        print(count,i,tmp+360-arr[i])
                        cv2.putText(openframe, "bad", (datum.poseKeypoints[mid][keyMid][0],datum.poseKeypoints[mid][keyMid][1]), cv2.FONT_HERSHEY_SIMPLEX, 1, (255,0,0), 3, cv2.LINE_AA)
                    
            elif abs(tmp-arr[i]) > 45:
                bad+=1
                if bad > 728:
                    cv2.putText(openframe, "bad", (datum.poseKeypoints[mid][keyMid][0],datum.poseKeypoints[mid][keyMid][1]), cv2.FONT_HERSHEY_SIMPLEX, 1, (255,0,0), 3, cv2.LINE_AA)
                    print(count,i)
                

            #print(bad)
            # else:
                # cv2.putText(openframe, "good", (datum.poseKeypoints[mid][keyMid][0],datum.poseKeypoints[mid][keyMid][1]), cv2.FONT_HERSHEY_SIMPLEX, 1, (255,0,0), 3, cv2.LINE_AA)

        cv2.putText(openframe, str(count), (30,30), cv2.FONT_HERSHEY_SIMPLEX, 1, (255,0,0), 3, cv2.LINE_AA)
        cv2.putText(openframe, str(people), (60,60), cv2.FONT_HERSHEY_SIMPLEX, 1, (255,0,0), 3, cv2.LINE_AA)
        cv2.imshow("frame", openframe)

        if out is None:
            fourcc = cv2.VideoWriter_fourcc(*"MJPG")
            out = cv2.VideoWriter('output.avi', fourcc, 30, (frame.shape[1], frame.shape[0]))
            # print(frame.shape[1], frame.shape[0]) #640*360

        out.write(openframe)

        # now = datetime.now()
        # current_time = now.strftime("%H:%M:%S")
        # print("End Time =", current_time)


        if cv2.waitKey(1) & 0xFF == ord('q'):
            cap.release()
            out.release()
            cv2.destroyAllWindows()
            break

        

    # Release
    #fw.close()
    cap.release()
    out.release()
    cv2.destroyAllWindows()

except Exception as e:
    print(e)
    if bad > 728:
        print(bad-728)
    else:
        print("0")
    sys.exit(-1)
