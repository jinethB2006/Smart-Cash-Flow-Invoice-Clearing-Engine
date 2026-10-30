
package com.smartcashflow;

import com.smartcashflow.api.DashboardApi;

public class Main {

    public static void main(String[] args) throws Exception {

        DashboardApi api = new DashboardApi();
        api.start();
    }
}
