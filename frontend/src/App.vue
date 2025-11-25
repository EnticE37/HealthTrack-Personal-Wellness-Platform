<template>
  <div class="container">
    <h1>HealthTrack 个人健康管理平台</h1>
    <p>示例前端，通过调用 Spring Boot 接口演示注册、预约、挑战与健康汇总功能。</p>

    <div class="section-grid">
      <div class="card">
        <h2>注册 / 登录</h2>
        <form @submit.prevent="register">
          <input v-model="registerForm.name" placeholder="姓名" required />
          <input v-model="registerForm.healthId" placeholder="Health ID" required />
          <input v-model="registerForm.email" placeholder="邮箱（可选）" />
          <input v-model="registerForm.phone" placeholder="电话（可选）" />
          <button type="submit">注册</button>
        </form>
        <form style="margin-top:12px" @submit.prevent="login">
          <input v-model="loginForm.healthId" placeholder="使用 Health ID 登录" required />
          <button type="submit">登录</button>
        </form>
        <div v-if="user">
          <p>当前用户：{{ user.name }} (ID: {{ user.id }})</p>
        </div>
      </div>

      <div class="card">
        <h2>账号信息与联系方式</h2>
        <form @submit.prevent="updateProfile">
          <input v-model="updateForm.name" placeholder="更新姓名" />
          <input v-model="updateForm.primaryProvider" placeholder="主治医生执业号" />
          <button type="submit" :disabled="!user">保存</button>
        </form>
        <form style="margin-top:10px" @submit.prevent="addEmail">
          <input v-model="contact.email" placeholder="新增邮箱" />
          <button type="submit" :disabled="!user">添加邮箱</button>
        </form>
        <form style="margin-top:10px" @submit.prevent="addPhone">
          <input v-model="contact.phone" placeholder="新增电话" />
          <button type="submit" :disabled="!user">添加电话</button>
        </form>
        <form style="margin-top:10px" @submit.prevent="linkProvider">
          <input v-model="provider.licenseNumber" placeholder="医生执业号" />
          <input v-model="provider.name" placeholder="医生姓名" />
          <button type="submit" :disabled="!user">关联医生</button>
        </form>
        <button style="margin-top:10px" @click="loadProviders" :disabled="!user">刷新关联列表</button>
        <ul class="list">
          <li v-for="p in providers" :key="p.id">{{ p.provider?.name }} - {{ p.provider?.licenseNumber }}</li>
        </ul>
      </div>
    </div>

    <div class="section-grid">
      <div class="card">
        <h2>预约</h2>
        <form @submit.prevent="bookAppointment">
          <input v-model="appointment.providerLicense" placeholder="医生执业号" required />
          <input v-model="appointment.consultationType" placeholder="咨询类型，如 In-Person" required />
          <input v-model="appointment.appointmentTime" type="datetime-local" required />
          <textarea v-model="appointment.notes" placeholder="备注"></textarea>
          <button type="submit" :disabled="!user">提交预约</button>
        </form>
        <button style="margin-top:10px" @click="loadAppointments" :disabled="!user">刷新预约列表</button>
        <ul class="list">
          <li v-for="a in appointments" :key="a.id">
            {{ a.consultationType }} @ {{ a.appointmentTime }} 状态: {{ a.status }}
            <button style="margin-left:8px" @click="cancelAppointment(a.id)">取消</button>
          </li>
        </ul>
      </div>

      <div class="card">
        <h2>健康挑战</h2>
        <form @submit.prevent="createChallenge">
          <input v-model="challenge.goal" placeholder="挑战目标" required />
          <input v-model="challenge.startDate" type="date" required />
          <input v-model="challenge.endDate" type="date" required />
          <button type="submit" :disabled="!user">创建挑战</button>
        </form>
        <form style="margin-top:10px" @submit.prevent="invite">
          <input v-model="invitation.contact" placeholder="邀请邮箱或电话" />
          <select v-model="invitation.contactType">
            <option value="EMAIL">EMAIL</option>
            <option value="PHONE">PHONE</option>
          </select>
          <button type="submit" :disabled="!user">发送邀请</button>
        </form>
        <button style="margin-top:10px" @click="loadChallenges">刷新挑战列表</button>
        <ul class="list">
          <li v-for="c in challenges" :key="c.id">{{ c.goal }} ({{ c.startDate }} - {{ c.endDate }})</li>
        </ul>
      </div>
    </div>

    <div class="section-grid">
      <div class="card">
        <h2>月度健康总结</h2>
        <form @submit.prevent="addMetric">
          <input v-model="metric.month" placeholder="月份，例如 2024-08" required />
          <input v-model.number="metric.averageWeight" type="number" step="0.1" placeholder="平均体重" />
          <input v-model="metric.averageBp" placeholder="平均血压" />
          <input v-model.number="metric.totalSteps" type="number" placeholder="总步数" />
          <button type="submit" :disabled="!user">保存月度指标</button>
        </form>
        <button style="margin-top:10px" @click="loadMetrics" :disabled="!user">查看记录</button>
        <ul class="list">
          <li v-for="m in metrics" :key="m.id">
            {{ m.metricMonth }}: 体重 {{ m.averageWeight }} kg / 血压 {{ m.averageBp }} / 步数 {{ m.totalSteps }}
          </li>
        </ul>
      </div>

      <div class="card">
        <h2>记录搜索</h2>
        <form @submit.prevent="searchAppointments">
          <input v-model="search.healthId" placeholder="按 Health ID" />
          <input v-model="search.providerLicense" placeholder="按执业号" />
          <input v-model="search.type" placeholder="类型" />
          <input v-model="search.start" type="datetime-local" />
          <input v-model="search.end" type="datetime-local" />
          <button type="submit">搜索预约</button>
        </form>
        <ul class="list">
          <li v-for="a in searchResults" :key="a.id">
            {{ a.user?.healthId || '未知用户' }} - {{ a.consultationType }} @ {{ a.appointmentTime }}
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'

const api = (path, options = {}) => fetch(path, { headers: { 'Content-Type': 'application/json' }, ...options }).then(r => r.json())

const user = ref(null)
const registerForm = reactive({ name: '', healthId: '', email: '', phone: '' })
const loginForm = reactive({ healthId: '' })
const updateForm = reactive({ name: '', primaryProvider: '' })
const contact = reactive({ email: '', phone: '' })
const provider = reactive({ licenseNumber: '', name: '' })
const appointment = reactive({ providerLicense: '', consultationType: '', appointmentTime: '', notes: '' })
const appointments = ref([])
const challenges = ref([])
const challenge = reactive({ goal: '', startDate: '', endDate: '' })
const invitation = reactive({ contact: '', contactType: 'EMAIL' })
const metrics = ref([])
const metric = reactive({ month: '', averageWeight: null, averageBp: '', totalSteps: null })
const providers = ref([])
const search = reactive({ healthId: '', providerLicense: '', type: '', start: '', end: '' })
const searchResults = ref([])

const register = async () => {
  const data = await api('/api/auth/register', { method: 'POST', body: JSON.stringify(registerForm) })
  user.value = data
}

const login = async () => {
  const res = await fetch('/api/auth/login', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(loginForm) })
  if (res.ok) user.value = await res.json()
}

const updateProfile = async () => {
  if (!user.value) return
  const body = {
    name: updateForm.name || undefined,
    primaryProviderLicense: updateForm.primaryProvider || undefined
  }
  const res = await fetch(`/api/account/${user.value.id}`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) })
  if (res.ok) user.value = await res.json()
}

const addEmail = async () => {
  if (!user.value || !contact.email) return
  await fetch(`/api/account/${user.value.id}/emails`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ value: contact.email }) })
  contact.email = ''
  await loadProviders()
}

const addPhone = async () => {
  if (!user.value || !contact.phone) return
  await fetch(`/api/account/${user.value.id}/phones`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ value: contact.phone }) })
  contact.phone = ''
  await loadProviders()
}

const linkProvider = async () => {
  if (!user.value || !provider.licenseNumber) return
  await fetch(`/api/account/${user.value.id}/providers`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(provider) })
  provider.licenseNumber = ''
  provider.name = ''
  await loadProviders()
}

const loadProviders = async () => {
  if (!user.value) return
  const res = await fetch(`/api/account/${user.value.id}/providers`)
  if (res.ok) providers.value = await res.json()
}

const bookAppointment = async () => {
  if (!user.value) return
  const payload = { ...appointment, userId: user.value.id }
  const res = await fetch('/api/appointments', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) })
  if (res.ok) await loadAppointments()
}

const loadAppointments = async () => {
  if (!user.value) return
  const res = await fetch(`/api/appointments/user/${user.value.id}`)
  if (res.ok) appointments.value = await res.json()
}

const cancelAppointment = async (id) => {
  await fetch(`/api/appointments/${id}/cancel`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ reason: '用户取消' }) })
  await loadAppointments()
}

const createChallenge = async () => {
  if (!user.value) return
  const payload = { ...challenge, creatorId: user.value.id, participantIds: [] }
  const res = await fetch('/api/challenges', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) })
  if (res.ok) await loadChallenges()
}

const loadChallenges = async () => {
  const res = await fetch('/api/challenges')
  if (res.ok) challenges.value = await res.json()
}

const invite = async () => {
  if (!user.value || !invitation.contact) return
  await fetch('/api/challenges/invite', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ ...invitation, inviterId: user.value.id }) })
  invitation.contact = ''
}

const addMetric = async () => {
  if (!user.value) return
  const payload = { metricMonth: metric.month, averageWeight: metric.averageWeight, averageBp: metric.averageBp, totalSteps: metric.totalSteps }
  await fetch(`/api/summary/metrics/${user.value.id}`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) })
  await loadMetrics()
}

const loadMetrics = async () => {
  if (!user.value) return
  const res = await fetch(`/api/summary/metrics/${user.value.id}`)
  if (res.ok) metrics.value = await res.json()
}

const searchAppointments = async () => {
  const params = new URLSearchParams()
  Object.entries(search).forEach(([k, v]) => {
    if (v) params.append(k, v)
  })
  const res = await fetch(`/api/search/appointments?${params.toString()}`)
  if (res.ok) searchResults.value = await res.json()
}
</script>
