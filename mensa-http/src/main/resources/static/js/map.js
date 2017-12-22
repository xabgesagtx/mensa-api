$(document).ready(function() {
    var mensaMap = L.map('mensa-map');
    var osmUrl = 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
    var osmAttrib = 'Map data © <a href="http://openstreetmap.org">OpenStreetMap</a> contributors';
    var osm = new L.TileLayer(osmUrl, {minZoom: 8, maxZoom: 18, attribution: osmAttrib});
    mensaMap.setView(new L.LatLng(53.5411, 10.0437),10);
    mensaMap.addLayer(osm);
    var markers = [];
    var nearestMensaButton = L.Control.extend({
        options: {
            position: 'topleft'
        },
        onAdd: function (map) {
            var container = L.DomUtil.create('div', 'leaflet-bar leaflet-control nearest-mensa');
            container.innerHTML = '<span class="glyphicon glyphicon-user"></span> nearest mensa';
            container.onclick = function(){
                if (navigator.geolocation) {
                    navigator.geolocation.getCurrentPosition(function (position) {
                        $.get('/rest/mensa/near', {longitude: position.coords.longitude, latitude: position.coords.latitude})
                            .done(function(mensa) {
                                mensaMap.setView(new L.LatLng(mensa.point.y, mensa.point.x),14);
                                $.each(markers, function(index, marker) {
                                   if (marker.mensaId === mensa.id) {
                                       marker.openPopup();
                                   }
                                });
                            })
                            .fail(function() {
                                alert('Could not find any mensa near you');
                            })
                        ;
                    });
                } else {
                    alert('Your browser does not support geolocation');
                }
            }
            return container;
        }

    });
   mensaMap.addControl(new nearestMensaButton());
   $.get('/rest/mensa')
        .done(function(data) {
            var tooltipTemplate = '<b><a href="{url}">{name}</a></b><br/>{address}<br/>{zipcode} {city}<br/><br/><a class="btn btn-default" href="{url}">View menu</a>';
            $.each(data, function(index,mensa) {
                if (mensa.point) {
                    var marker = L.marker([mensa.point.y, mensa.point.x]).addTo(mensaMap);
                    var tooltipData = {
                        url : '/mensa/' + mensa.id,
                        name : mensa.name,
                        address : mensa.address,
                        zipcode : mensa.zipcode,
                        city : mensa.city
                    };
                    marker.bindPopup(L.Util.template(tooltipTemplate, tooltipData));
                    marker.mensaId = mensa.id;
                    markers.push(marker);
                }
            });
        })
       .fail(function() {
           alert('Could not load mensa data');
       })
});