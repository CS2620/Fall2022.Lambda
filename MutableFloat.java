public class MutableFloat {
    float value;
    public MutableFloat(float value){
      this.value = value;
    }

    public float setValue(float value){
      this.value = value;
      return this.value;
    }

    public float getValue(){
      return this.value;
    }

    @Override
    public String toString() {
      return "" + this.value;
    }
}
