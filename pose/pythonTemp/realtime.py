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

diff = 728  #容錯綠
scoreDiff = 114.75 #分數容錯
totalCount = 765*15

start = 73.66   #肩膀開始的角度
start1 = 347.2  #手腕開始的角度
angle = 45      #角度正常範圍
startcheck = 0       #開始了沒


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

    opWrapper = op.WrapperPython()
    opWrapper.configure(params)
    opWrapper.start()

    datum = op.Datum()
    cap = cv2.VideoCapture(0);
    fr = open("point/pointTwticeL1_7fps.txt",'r')
    count = 0   #第幾幀數
    flag=0  #符合每個點的xy都不為0(128-138行)
    arr = [0]*15 #15個角度，每個角度由三個關節點算出
    bad=0       #錯誤次數
    badcheck=0  #是否錯誤
    out=None    #影片輸出

    now = datetime.now()
    current_time = now.strftime("%H:%M:%S")
    print("Current Time =", current_time)  

    while True:
        ret, frame = cap.read() #640x480
        if ret == False:
            print("error")
            break

        img = frame
        datum.cvInputData = img

        opWrapper.emplaceAndPop([datum])
        
        openframe = datum.cvOutputData

        if(str(datum.poseKeypoints) == "2.0" or str(datum.poseKeypoints) == "0.0"): #無偵測到人
            continue

        #start check
        if not startcheck:
            tmp = calAngle(datum.poseKeypoints[0][1], datum.poseKeypoints[0][2], datum.poseKeypoints[0][3])
            tmp1 = calAngle(datum.poseKeypoints[0][2], datum.poseKeypoints[0][3], datum.poseKeypoints[0][4])
            if not (tmp < start+angle and start-angle < tmp and (tmp1<33 or start1-angle < tmp1)): #起始動作沒有對齊
                # cv2.putText(openframe, str(count), (30,30), cv2.FONT_HERSHEY_SIMPLEX, 1, (255,0,0), 3, cv2.LINE_AA)
                cv2.imshow("frame", openframe)
                if cv2.waitKey(1) & 0xFF == ord('q'):
                    break
                continue;
            else:
                p=subprocess.Popen("python runvideo.py", shell=True)
                print("start")
                startcheck = 1 #影片開始

        count += 1
        inp = fr.readline().replace('\n','')    #讀檔取幀數，以換行區隔ㄗ
        #計算角度
        arr[0] = calAngle(datum.poseKeypoints[0][0], datum.poseKeypoints[0][1], datum.poseKeypoints[0][2])
        arr[1] = calAngle(datum.poseKeypoints[0][2], datum.poseKeypoints[0][1], datum.poseKeypoints[0][8])
        arr[2] = calAngle(datum.poseKeypoints[0][8], datum.poseKeypoints[0][1], datum.poseKeypoints[0][5])
        arr[3] = calAngle(datum.poseKeypoints[0][5], datum.poseKeypoints[0][1], datum.poseKeypoints[0][0])

        arr[4] = calAngle(datum.poseKeypoints[0][1], datum.poseKeypoints[0][2], datum.poseKeypoints[0][3])
        arr[5] = calAngle(datum.poseKeypoints[0][2], datum.poseKeypoints[0][3], datum.poseKeypoints[0][4])

        arr[6] = calAngle(datum.poseKeypoints[0][1], datum.poseKeypoints[0][5], datum.poseKeypoints[0][6])
        arr[7] = calAngle(datum.poseKeypoints[0][7], datum.poseKeypoints[0][6], datum.poseKeypoints[0][5])

        arr[8] = calAngle(datum.poseKeypoints[0][1], datum.poseKeypoints[0][8], datum.poseKeypoints[0][9])
        arr[9] = calAngle(datum.poseKeypoints[0][12], datum.poseKeypoints[0][8], datum.poseKeypoints[0][1])
        arr[10] = calAngle(datum.poseKeypoints[0][9], datum.poseKeypoints[0][8], datum.poseKeypoints[0][12])

        arr[11] = calAngle(datum.poseKeypoints[0][10], datum.poseKeypoints[0][9], datum.poseKeypoints[0][8])
        arr[12] = calAngle(datum.poseKeypoints[0][9], datum.poseKeypoints[0][10], datum.poseKeypoints[0][11])

        arr[13] = calAngle(datum.poseKeypoints[0][8], datum.poseKeypoints[0][12], datum.poseKeypoints[0][13])
        arr[14] = calAngle(datum.poseKeypoints[0][14], datum.poseKeypoints[0][13], datum.poseKeypoints[0][12])

        for i in range(0,15):
            tmp = (float)(fr.readline().replace('\n',''))   #讀檔取角度
            if(tmp<0 or arr[i]<0):  #角度小於0，跳過
                continue;
            #i對應到的中間關節的編號
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

            #依角度判斷對錯(45度)
            if tmp >= 315 and arr[i] < 45:
                if arr[i]+360-tmp > 45:
                    badcheck = 1
            elif arr[i] >= 315 and tmp < 45:
                if tmp+360-arr[i] > 45:
                    badcheck = 1
            elif abs(tmp-arr[i]) > 45:
                badcheck = 1

            #計算扣幾分
            if badcheck:
                countdiv = count % 7
                if(countdiv == 2 or countdiv == 6):
                    bad += 5
                else:
                    bad += 4
                badcheck = 0;
                print(count,i)
                cv2.putText(openframe, "bad", (datum.poseKeypoints[0][keyMid][0],datum.poseKeypoints[0][keyMid][1]), cv2.FONT_HERSHEY_SIMPLEX, 1, (255,0,0), 3, cv2.LINE_AA)
                    
        #輸出幀數, 人數, 畫面
        cv2.putText(openframe, str(count), (30,30), cv2.FONT_HERSHEY_SIMPLEX, 1, (255,0,0), 3, cv2.LINE_AA)
        cv2.imshow("frame", openframe)

        #輸出影片
        if out is None:
            fourcc = cv2.VideoWriter_fourcc(*"MJPG")
            out = cv2.VideoWriter('output.avi', fourcc, 7, (frame.shape[1], frame.shape[0]))
            # print(frame.shape[1], frame.shape[0]) #640*360

        out.write(openframe)

        #釋放資源
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    print(bad-diff);
    print((totalCount-bad+diff)/scoreDiff);

    # Release
    now = datetime.now()
    current_time = now.strftime("%H:%M:%S")
    print("Current Time =", current_time)

    cap.release()
    out.release()
    cv2.destroyAllWindows()

except Exception as e:
    print(e)

    print(bad-diff);
    score = (totalCount-bad+diff)/scoreDiff
    print(50+(score-80)*2.5);
    cap.rlease()
    cv2.destroyAllWindows()
    sys.exit(-1)