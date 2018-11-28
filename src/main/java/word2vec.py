# -*- encoding=utf-8 -*-

import gensim
import jieba
import numpy as np
from gevent import monkey
from scipy.linalg import norm

monkey.patch_all()
# model = gensim.models.KeyedVectors.load_word2vec_format('./Tencent_AILab_ChineseEmbedding/Tencent_AILab_ChineseEmbedding.txt', binary=False)
model = gensim.models.KeyedVectors.load_word2vec_format('./tmp.txt', binary=False)


def get_sentence_sim(sen1, sen2):
    def sentence_vector(s):
            words = jieba.lcut(s)
            v = np.zeros(64)
            for word in words:
                v += model[word]
            v /= len(words)
            return v
    v1, v2 = sentence_vector(sen1), sentence_vector(sen2)
    return np.dot(v1, v2) / (norm(v1) * norm(v2))


def get_sim(word1,word2):
    try:
        sim = model.similarity(word1, word2)
        return sim
    except:
        return 'wrong word'


def get_vec(word):
    try:
        strin = model[word]
    except:
        return word + 'not found'
    return strin


#if __name__ == '__main__':
    #app.run(host='0.0.0.0',port=5005)
#    server = pywsgi.WSGIServer(('0.0.0.0', 5005), app)
#    server.serve_forever()
get_sentence_sim("你好", "好久不见")