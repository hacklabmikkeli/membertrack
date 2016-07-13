#!/bin/sh
cat << EOF
application:
    debug: false
debugServer:
    enabled: false
databaseInitializer:
    setupList: setup_list_prod.txt
    enabled: true
dataSourceInitializer:
    connectionString: jdbc:h2:$MEMBERTRACK_DB_PATH;AUTO_SERVER=TRUE
    username: sa
    password: sa
    rdbms: H2
    jndiName: jdbc/membertrack
    enabled: true
holviSynchronizer:
    interval: 6
    intervalUnit: HOURS
    enabled: true
    populator:
        authToken: $HOLVI_AUTH_TOKEN
        poolHandle: zXgpv9
        interval: 30
        productMappings:

          - productCode: 0634e0dd8c49bb226d9632e7a1f560bd
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 360
            timeUnit: DAY
            payment: 600.00

          - productCode: 2aa682f9496606f32056ce08b4e66591
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 180
            timeUnit: DAY
            payment: 300.00

          - productCode: 34fbdab9c98a6ad2b96a19247961ddac
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 90
            timeUnit: DAY
            payment: 150.00

          - productCode: 4e618a1a0fe0ced32bcd601670f1f9d3
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 30
            timeUnit: DAY
            payment: 50.00

          - productCode: fce04fdf9ff395f4ee776b8a983488c6
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 360
            timeUnit: DAY
            payment: 540.00

          - productCode: 4517f14fc1f6e722312b699c627a8f9f
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 180
            timeUnit: DAY
            payment: 270.00

          - productCode: 0ca659bc8b270a49449af42c12f93ea9
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 90
            timeUnit: DAY
            payment: 135.00

          - productCode: e4284f16e2882736ee640356d131d60e
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 30
            timeUnit: DAY
            payment: 45.00

          - productCode: d7b5726a86e9eff26fd0b4d162ac294a
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 360
            timeUnit: DAY
            payment: 480.00

          - productCode: b091aec77805b4616bb8c1e07cd4ce46
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 180
            timeUnit: DAY
            payment: 240.00

          - productCode: bb7d44d62fb51e357f4f21c46b2d9a4c
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 90
            timeUnit: DAY
            payment: 120.00

          - productCode: f083a7d0b56978cc59086b3a9de9c916
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 30
            timeUnit: DAY
            payment: 40.00
     
          - productCode: 21614831ad0955df78701faef68e3080
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 360
            timeUnit: DAY
            payment: 420.00
     
          - productCode: a0110382d8ce53c4d9915419694126bc
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 180
            timeUnit: DAY
            payment: 210.00

          - productCode: e6141759563cd95be5b1163b1963207c
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 90
            timeUnit: DAY
            payment: 105.00

          - productCode: 14b9437be388cdfc811479a586c30893
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 30
            timeUnit: DAY
            payment: 35.00

          - productCode: 09b97a758b82de8bb6e80627b482da8c
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 360
            timeUnit: DAY
            payment: 360.00

          - productCode: 7ac0b02552e1056f0321057d8a39de73
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 180
            timeUnit: DAY
            payment: 180.00

          - productCode: e57a05ce4988cf824adc574a167ad38b
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 90
            timeUnit: DAY
            payment: 90.00

          - productCode: 03f83cf94cd023418bd572490c33f7de
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 30
            timeUnit: DAY
            payment: 30.00

          - productCode: 1bb06ca0606a075b74d0eef43c7d16d6
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 360
            timeUnit: DAY
            payment: 300.00

          - productCode: 9ec7230a8cc3ef7e7c30bf5159a1b3e1 
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 180
            timeUnit: DAY
            payment: 150.00

          - productCode: 583be4d843e2cc69595277e47f05ecc5
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 90
            timeUnit: DAY
            payment: 75.00

          - productCode: 47b6a62b8847e2a382c10a220ea1f12d
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 30
            timeUnit: DAY
            payment: 25.00

          - productCode: 035ea4cc10bd73bc7fb5cde5ecb2289c
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 360
            timeUnit: DAY
            payment: 240.00

          - productCode: f1a540f39ba77096cf770ae29c65ed29
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 180
            timeUnit: DAY
            payment: 120.00

          - productCode: 4453bca3f98e3082cdcd61b08d2154dd
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 90
            timeUnit: DAY
            payment: 60.00

          - productCode: ea79b2c8290c382c0cb60654bb5a7ed0
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 30
            timeUnit: DAY
            payment: 20.00

          - productCode: 21129142094fe8f47ff40e746e6781e0
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 360
            timeUnit: DAY
            payment: 180.00

          - productCode: 9ec6852aedc4f3e4401a2f6fead3d34c
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 180
            timeUnit: DAY
            payment: 90.00

          - productCode: fe82fdc4d15ee02970762f1615c7bb0a
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 90
            timeUnit: DAY
            payment: 45.00

          - productCode: 940d922ace37eb4f0de4ae3286b2f9f6
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 30
            timeUnit: DAY
            payment: 15.00

          - productCode: 8ffe6eb391749b83328894b22cb74fe7
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 360
            timeUnit: DAY
            payment: 120.00

          - productCode: e541f8074aeadedd1fdc5a5dba2a19c3
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 180
            timeUnit: DAY
            payment: 60.00

          - productCode: c9861bb4902369203a8455ee8ec4008b
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 90
            timeUnit: DAY
            payment: 30.00

          - productCode: cb1c500141f1146e698770ca6fc9fa73
            serviceUUID: 798aacc6-aafa-4d76-b503-18b2653616ef
            length: 30
            timeUnit: DAY
            payment: 10.00

          - productCode: 8b2a67ea30ddcc96eaac32c7333f3d2c
            serviceUUID: bee4ce2c-7200-4125-874f-2e6c5cca6343
            length: 1
            timeUnit: YEAR
            payment: 20.00
EOF
