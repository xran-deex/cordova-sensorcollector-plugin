

module.exports = {
    start: function(sensor, type, callback){
        switch(sensor){
            case 'wifi':
                cordova.exec(callback, function(err){
                    callback('oops');
                }, "WifiCollector", "scan", []);
                break;
            case 'cell':
                cordova.exec(callback, function(err){
                    callback('oops');
                }, "CellCollector", "start", []);
                break;
            case 'geomagnet':
                cordova.exec(callback, function(err){
                    callback('oops');
                }, "GeomagneticPlugin", "start", [type]);
                break;
            default:
        }
    },
    stop: function(sensor, type, callback){
        switch(sensor){
            case 'wifi':
                cordova.exec(callback, function(err){
                    callback('oops');
                }, "WifiCollector", "stop", []);
                break;
            case 'cell':
                cordova.exec(callback, function(err){
                    callback('oops');
                }, "CellCollector", "stop", []);
                break;
            case 'geomagnet':
                cordova.exec(callback, function(err){
                    callback('oops');
                }, "GeomagneticPlugin", "stop", [type]);
                break;
            default:
        }
    },
    /**
     *  Configures the type of sensor to listen to
     *  @param {Integer} delay The delay amt as a int flag
     *  @param {Integer} type The sensor type to listen to
     */
    configure: function(sensor, delay, type, callback){
        switch(sensor){
            case 'geomagnet':
                cordova.exec(callback, function(err){
                    callback(err);
                }, "GeomagneticPlugin", "configure", [delay, type]);
                break;
            default:
        }
    }
};
