function() {
    var typesMap={};

    var agg=function(value, val, col) {
                typesMap[value]= (typesMap[value] || 0)+1;
    }

    if(this.flags.i038.value){
        this.flags.i038.types.forEach(agg);
    }

    if(this.flags.i007.value) {
        this.flags.i007.types.forEach(agg);
    }

    if(this.flags.i019.value) {
        this.flags.i019.types.forEach(agg);
    }

    if(this.flags.i077.value) {
        this.flags.i077.types.forEach(agg);
    }

    if(this.flags.i180.value) {
        this.flags.i180.types.forEach(agg);
    }

    if(this.flags.i004.value) {
            this.flags.i004.types.forEach(agg);
    }

    if(this.flags.i003.value) {
                this.flags.i003.types.forEach(agg);
    }

    for(var key in typesMap) {
        emit(key,typesMap[key]);
    }

};