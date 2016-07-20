/* global InstantClick */
"use strict";

InstantClick.on('change', function() {
    $(".use-datepicker").datepicker({
        dateFormat: 'd.m.yy',
        showButtonPanel: true
    });
});
