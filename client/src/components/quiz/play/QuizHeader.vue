<template>
<div>
  <span id="animationSandbox" style="display: block;" class>
    <h1 class="site__title mega">{{ this.quiz.meta.title }}</h1>
    <h2>{{ this.quiz.meta.description }} - {{ this.quiz.questions.length }} questions</h2>
  </span>
  <p class="beta subhead">subject: {{ this.quiz.meta.subject }}
  </p>
  <q-btn v-if="this.quizProgress === 'intro' || this.quizProgress === 'scores'"
    class="actions text-h7 text-orange-9"
    outline no-caps color="white"
    label="Start quiz"
    @click="startQuizChild()">
  </q-btn>
  <q-btn v-else
    class="actions text-h7 text-orange-9"
    outline no-caps color="white"
    @click="stopQuizChild()"
    label="Stop quiz">
  </q-btn>
    <!-- <button
    v-else
    @click="toggleFavorite"
    :class="{
      'btn-primary': quiz.favorited,
      'btn-outline-primary': !quiz.favorited
    }"
    >
    <i class="ion-heart"></i>
    <span class="counter"> {{ quiz.favoritesCount }} </span>
    </button> -->
</div>
</template>

<script type="text/javascript">
import { mapGetters } from 'vuex';
import { FAVORITE_ADD, FAVORITE_REMOVE } from 'src/store/types/actions.type';

export default {
  name: 'QuizHeader',
  props: [
    'quiz',
    'quizProgress',
    'startQuizParent',
    'stopQuizParent',
  ],
  computed: {
    ...mapGetters(['currentUser', 'isAuthenticated']),
  },
  methods: {
    startQuizChild() {
      this.$emit('startQuizParent');
    },
    stopQuizChild() {
      this.$emit('stopQuizParent');
    },
    isCurrentUser() {
      if (this.currentUser.username && this.quiz.author.username) {
        return this.currentUser.username === this.quiz.author.username;
      }
      return false;
    },
    toggleFavorite() {
      if (!this.isAuthenticated) {
        this.$router.push({ name: 'login' });
        return;
      }
      const action = this.quiz.favorited ? FAVORITE_REMOVE : FAVORITE_ADD;
      this.$store.dispatch(action, this.quiz.quizId);
    },
  },
};
</script>

<style scoped>
/*-----------------------------------*\
  $TYPOGRAPHY
\*-----------------------------------*/
h1,
.alpha {
  margin-bottom: 1rem;
  font-size: 3rem;
  font-weight: 100;
  line-height: 1;
  letter-spacing: -0.05em;
  text-align: center;
}
h2,
.beta {
  margin-bottom: 0.75rem;
  font-weight: 350;
  font-size: 1rem;
  line-height: 1;
  text-align: center;
}
@media (min-width: 650px) {
  .mega {
    font-size: 6rem;
    line-height: 1;
  }
}
.subhead,
.answer {
  color: #7b8993;
}
.question {
  color: #f35626;
  font-size: 1rem;
}
.promo {
  text-align: center;
}
p,
hr,
form {
  margin-bottom: 1.5rem;
}
hr {
  border: none;
  margin-top: -1px;
  height: 1px;
  background-color: #ff6600;
  background-image: linear-gradient(
    to right,
    #ffffff 0%,
    #c0c8c9 50%,
    #ffffff 100%
  );
}
a {
  color: inherit;
  text-decoration: underline;
  animation: hue 60s infinite linear;
}
a:hover {
  color: #f35626;
}
</style>
