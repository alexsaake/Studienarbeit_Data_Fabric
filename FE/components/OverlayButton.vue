<template>
  <div class="custom-button" @click="customAction">
    <i class="fas fa-plus"></i>
  </div>
</template>

<script>
export default {
  methods: {
    customAction() {
      this.$emit('custom-click');
    }
  }
};
</script>

<style>
.custom-button{
    position: fixed;
    bottom: 100px;
    right: 100px;
    background-color: #007BFF;
    color: white;
    border: none;
    border-radius: 50% !important;
    width: 70px;
    height: 70px;
    cursor: pointer;
}
@media screen and (max-width: 600px) {
    .custom-button{
        bottom: 80px;
        right: 60px;
    }
}
.custom-button:after {
    content: '';
    position: absolute;
    transform: translate(-50%, -50%);
    height: 4px;
    width: 50%;
    background: white;
    top: 50%;
    left: 50%;
}

.custom-button:before {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background: white;
    height: 50%;
    width: 4px;
}
</style>
