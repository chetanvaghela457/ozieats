package com.admin.ozieats_app.utils;

import java.io.IOException;

public class NoConnectivityException extends IOException {

    @Override
    public String getMessage() {
        return "No Internet Available!!";
        //return "";
    }
}
