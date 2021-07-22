from socket import *
import numpy as np
import jpysocket
import cv2
import base64
import time

def main():
    HOST = '192.168.0.106'
    PORT = 5555
    BUFSIZ = 1024*20
    ADDR = (HOST, PORT)
    tcpSerSock = socket(AF_INET, SOCK_STREAM)
    tcpSerSock.bind(ADDR)
    tcpSerSock.listen(5)
    while True:
        rec_d = bytes([])
        print('waiting for connection...')
        tcpCliSock, addr = tcpSerSock.accept()
        print('...connected from:', addr)
        tcpCliSock.send(jpysocket.jpyencode('StartSend'))
        
        rec = tcpCliSock.recv(1024)
        rec = jpysocket.jpydecode(rec)
        
        print(rec)
        if rec == "face":
            while True:
                data = tcpCliSock.recv(BUFSIZ)
                if not data or len(data) == 0:
                    break
                else:
                    rec_d = rec_d + data

            path = 'D:/task/d.txt'
            f = open(path, 'w')
            f.write(str(rec_d))
            f.close()
            
            with open("D:/task/d.txt","r") as f:
                img = base64.b64decode(f.read()[1:])
                print(type(f.read()))
                fh = open("D:/task/pic_2_sucess22.jpg","wb")
                fh.write(img)
                fh.close()
            time.sleep(1)
            fa = open("D:/task/rec.txt","r")
            rec_send = fa.readline()
            print(rec_send)
            tcpCliSock.close()
            tcpCliSock, addr = tcpSerSock.accept()
            tcpCliSock.send(jpysocket.jpyencode(rec_send))
            print('send complete')
            tcpCliSock.close()
        
        
            
    tcpSerSock.close()

if __name__ == "__main__":
    main()