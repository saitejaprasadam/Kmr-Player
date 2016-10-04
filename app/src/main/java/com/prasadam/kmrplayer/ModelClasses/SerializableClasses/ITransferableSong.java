package com.prasadam.kmrplayer.ModelClasses.SerializableClasses;

import com.prasadam.kmrplayer.ModelClasses.TransferableSong;

/*
 * Created by Prasadam Saiteja on 10/3/2016.
 */

public class ITransferableSong extends TransferableSong{

    private static final long serialVersionUID = 123456789L;

    public ITransferableSong(ISong song, String client_mac_address) {
        super(song, client_mac_address);
    }
    public ITransferableSong(String client_mac_address, String hashID, long id) {
        super(client_mac_address, hashID, id);
    }
}
