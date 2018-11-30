# -*- coding: utf-8 -*-
import time
import requests
import json


def test():
    url = "http://localhost:21628/paraIdentify"
    sentence = "深圳有什么好玩的";
    parameter = {'triggerSentence': sentence}
    headers = {'Content-type': 'application/json'}
    try:
        r = requests.post(url, data=json.dumps(
            parameter), headers=headers, timeout=20)
        if r.status_code == 200:
            data = r.json()
            # print(data)
            result_info = data['target']
            print(result_info)
        else:
            print ("wrong,status_code: ", r.status_code)
    except Exception as e:
        print (Exception, ' : ', e)


if __name__ == '__main__':
    test()