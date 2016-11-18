function() {
    var tenderersSet = new Set();
    this.tender.tenderers.forEach(function(tenderer, it, array) {
        tenderersSet.add(tenderer._id);
    });

    if (tenderersSet.size > 1) {
        var arr = Array.from(tenderersSet);
        for (var i = 0; i < arr.length; i++) {
            for (var j = i + 1; j < arr.length; j++)
                if (arr[i].localeCompare(arr[j]) > 0)
                    emit({
                        tendererId1: arr[i],
                        tendererId2: arr[j]
                    }, 1);
                else
                    emit({
                        tendererId1: arr[j],
                        tendererId2: arr[i]
                    }, 1);
        }
    }
};