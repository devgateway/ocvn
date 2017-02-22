function() {
    var noFlags=0;

    if(this.flags.i038.value){
        noFlags++;
    }

    if(this.flags.i007.value) {
        noFlags++;
    }

    if(this.flags.i019.value) {
        noFlags++;
    }

    if(this.flags.i077.value) {
        noFlags++;
    }

    if(this.flags.i180.value) {
        noFlags++;
    }

    emit('all',noFlags);

};