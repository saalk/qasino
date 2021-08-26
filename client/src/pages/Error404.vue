<template>
  <div class="no-padding no-margin fixed-center text-center">
    <p>
      <img src="https://imgs.xkcd.com/comics/star_ratings.png">
    </p>
    <br>
    <p>
      Please supply a review in the Play Store
      to motivate me finishing this app :-)</p>
    <q-btn class="actions" dense outline text-color="grey" to="/" label="Go back" no-caps></q-btn>

    <q-btn type="a" href="https://play.google.com/store/apps/details?id=cloud.casino.cards.quiz"
    class="actions" dense outline text-color="grey" label="Rate me" no-caps></q-btn>

  </div>
</template>

<script>
export default {
  name: 'Error404',
};
</script>

<style scoped>
.actions{
    /* no underlining */
    /* text-decoration: none; */
    margin-right: 5px
}
</style>
