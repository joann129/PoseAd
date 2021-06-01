import socket
import jpysocket

host = '192.168.0.105'  # 對server端為主機位置
port = 5555
# host = socket.gethostname()

address = (host, port)

socket01 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
# AF_INET:默認IPv4, SOCK_STREAM:TCP

socket01.bind(address)  # 讓這個socket要綁到位址(ip/port)
socket01.listen(5)  # listen(backlog)
# backlog:操作系統可以掛起的最大連接數量。該值至少為1，大部分應用程序設為5就可以了
print('Socket Startup')

conn, addr = socket01.accept()  # 接受遠程計算機的連接請求，建立起與客戶機之間的通信連接
# 返回（conn,address)
# conn是新的套接字對象，可以用來接收和發送數據。address是連接客戶端的地址
print('Connected by', addr)

##################################################
# 傳送開始傳送的訊號

# 開始接收
msgsend=jpysocket.jpyencode("StartSend\n")
conn.send(msgsend)
buf = conn.recv(1024)
buf = jpysocket.jpydecode(buf)
print(buf)
# conn.send(msgsend)
print('begin write image file "moonsave.png"')
imgFile = open('moonsave.png', 'w')  # 開始寫入圖片檔
while True:
    imgData = conn.recv(512)  # 接收遠端主機傳來的數據
    if not imgData:
        break  # 讀完檔案結束迴圈
    imgFile.write(imgData)
imgFile.close()
print(imgData.decode('utf-8'))
print('image save')
##################################################

conn.close()  # 關閉
socket01.close()
print('server close')