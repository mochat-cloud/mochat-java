package com.mochat.mochat.model.wm;

public class ChatRsaKeyModel {

    private String version;
    private String publicKey;
    private String privateKey;

    public int getVersion() {
        try {
            return Integer.parseInt(version);
        } catch (Exception e) {
            return -1;
        }
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
