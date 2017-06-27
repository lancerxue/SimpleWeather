package com.example.dennis.jdtq;

import java.util.List;

/**
 * Created by dennis on 2017/6/21.
 */

public interface PermissionListener {
     void onGranted();
    
     void onDenied(List<String> deniedPermissions);
    
}
