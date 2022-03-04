package com.mochat.mochat.model.wm;

import java.util.List;

public class DataResultModel {

    private int errcode;
    private String errmsg;
    private List<ChatdataDTO> chatdata;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public List<ChatdataDTO> getChatdata() {
        return chatdata;
    }

    public void setChatdata(List<ChatdataDTO> chatdata) {
        this.chatdata = chatdata;
    }

    public static class ChatdataDTO {

        private int seq;
        private String msgid;
        private int publickey_ver;
        private String encrypt_random_key;
        private String encrypt_chat_msg;

        public int getSeq() {
            return seq;
        }

        public void setSeq(int seq) {
            this.seq = seq;
        }

        public String getMsgid() {
            return msgid;
        }

        public void setMsgid(String msgid) {
            this.msgid = msgid;
        }

        public int getPublickey_ver() {
            return publickey_ver;
        }

        public void setPublickey_ver(int publickey_ver) {
            this.publickey_ver = publickey_ver;
        }

        public String getEncrypt_random_key() {
            return encrypt_random_key;
        }

        public void setEncrypt_random_key(String encrypt_random_key) {
            this.encrypt_random_key = encrypt_random_key;
        }

        public String getEncrypt_chat_msg() {
            return encrypt_chat_msg;
        }

        public void setEncrypt_chat_msg(String encrypt_chat_msg) {
            this.encrypt_chat_msg = encrypt_chat_msg;
        }
    }
}
