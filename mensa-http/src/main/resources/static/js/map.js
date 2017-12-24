$(document).ready(function() {
    var mensaMap = L.map('mensa-map');
    var osmUrl = 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
    var osmAttrib = 'Map data © <a href="http://openstreetmap.org">OpenStreetMap</a> contributors';
    var osm = new L.TileLayer(osmUrl, {minZoom: 8, maxZoom: 18, attribution: osmAttrib});
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
                                $.each(markers, function(index, marker) {
                                   if ($.inArray(mensa.id,marker.mensaIds) > -1) {
                                       mensaMap.setView(new L.LatLng(mensa.point.y, mensa.point.x),14);
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
                    var tooltipData = {
                        url : '/mensa/' + mensa.id,
                        name : mensa.name,
                        address : mensa.address,
                        zipcode : mensa.zipcode,
                        city : mensa.city
                    };
                    var tooltipText = L.Util.template(tooltipTemplate, tooltipData);
                    var marker = undefined;
                    $.each(markers, function(index, value) {
                        var latLng = value.getLatLng();
                        if (latLng.lat.toPrecision(5) === mensa.point.y.toPrecision(5) && latLng.lng.toPrecision(5) === mensa.point.x.toPrecision(5)) {
                            var oldContent = value.getPopup().getContent();
                            value.bindPopup(tooltipText + '<br/><br/>' + oldContent);
                            value.mensaIds.push(mensa.id);
                            marker = value;
                        }
                    });
                    if (marker === undefined) {
                        marker = L.marker([mensa.point.y, mensa.point.x]).addTo(mensaMap);
                        marker.bindPopup(tooltipText);
                        marker.mensaIds = [mensa.id];
                        markers.push(marker);
                    }
                }
            });
            var group = new L.featureGroup(markers);
            mensaMap.fitBounds(group.getBounds().pad(0.5));
        })
       .fail(function() {
           alert('Could not load mensa data');
       })
});