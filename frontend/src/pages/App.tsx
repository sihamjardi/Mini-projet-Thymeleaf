import React, { useEffect, useMemo, useState } from 'react'
import { Container, Nav, Navbar, Row, Col, Card, Table, Form } from 'react-bootstrap'
import { Chart, ArcElement, BarElement, CategoryScale, LinearScale, Tooltip, Legend } from 'chart.js'

Chart.register(ArcElement, BarElement, CategoryScale, LinearScale, Tooltip, Legend)

type Client = { id: number; nom: string; telephone: string }
type TableResto = { id: number; numero: number; places: number; zone: string }
type Reservation = {
  pk: { client: number; tablersto: number; dateHeure: string }
  nbCouverts: number
  statut: string
  tablersto: TableResto
  client: Client
}

export default function App() {
  const [date, setDate] = useState<string>(new Date().toISOString().slice(0, 10))
  const [reservations, setReservations] = useState<Reservation[]>([])
  const [stats, setStats] = useState<{ midi: number; soir: number; noShow: number } | null>(null)

  useEffect(() => {
    fetch(`/api/reservations?date=${date}`).then(r => r.json()).then(setReservations)
  }, [date])

  useEffect(() => {
    fetch(`/api/reservations/stats?date=${date}`).then(r => r.json()).then(data => {
      setStats({ midi: data.midi, soir: data.soir, noShow: data.noShow })
    })
  }, [date])

  const occData = useMemo(() => ({
    labels: ['Midi', 'Soir'],
    datasets: [{ label: "Taux d'occupation", data: [(stats?.midi ?? 0) * 100, (stats?.soir ?? 0) * 100], backgroundColor: ['#4e79a7', '#f28e2b'] }]
  }), [stats])

  const noShowData = useMemo(() => ({
    labels: ['No-show', 'Présents'],
    datasets: [{ data: [(stats?.noShow ?? 0) * 100, 100 - (stats?.noShow ?? 0) * 100], backgroundColor: ['#e15759', '#76b7b2'] }]
  }), [stats])

  return (
    <>
      <Navbar bg="dark" variant="dark">
        <Container>
          <Navbar.Brand>Restaurant</Navbar.Brand>
          <Nav className="me-auto">
            <Nav.Link href="#reservations">Réservations</Nav.Link>
            <Nav.Link href="#stats">Statistiques</Nav.Link>
          </Nav>
        </Container>
      </Navbar>
      <Container className="mt-4">
        <Row className="mb-3">
          <Col md="4">
            <Form.Label>Date</Form.Label>
            <Form.Control type="date" value={date} onChange={e => setDate(e.target.value)} />
          </Col>
        </Row>

        <Row>
          <Col md="7">
            <Card id="reservations">
              <Card.Header>Réservations</Card.Header>
              <Card.Body>
                <Table size="sm" striped hover>
                  <thead>
                    <tr>
                      <th>Date/Heure</th>
                      <th>Client</th>
                      <th>Table</th>
                      <th>Zone</th>
                      <th>Nb</th>
                      <th>Statut</th>
                    </tr>
                  </thead>
                  <tbody>
                    {reservations.map(r => (
                      <tr key={`${r.pk.client}-${r.pk.tablersto}-${r.pk.dateHeure}`}>
                        <td>{new Date(r.pk.dateHeure).toLocaleString()}</td>
                        <td>{r.client?.nom}</td>
                        <td>{r.tablersto?.numero}</td>
                        <td>{r.tablersto?.zone}</td>
                        <td>{r.nbCouverts}</td>
                        <td>{r.statut}</td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              </Card.Body>
            </Card>
          </Col>
          <Col md="5" id="stats">
            <Card className="mb-3">
              <Card.Header>Taux d'occupation</Card.Header>
              <Card.Body>
                <canvas id="occChart"></canvas>
              </Card.Body>
            </Card>
            <Card>
              <Card.Header>No-show</Card.Header>
              <Card.Body>
                <canvas id="noShowChart"></canvas>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>

      {/* Render charts after DOM mounts */}
      <ChartMount occData={occData} noShowData={noShowData} />
    </>
  )
}

function ChartMount({ occData, noShowData }: { occData: any; noShowData: any }) {
  useEffect(() => {
    const occCtx = (document.getElementById('occChart') as HTMLCanvasElement)?.getContext('2d')!
    const occ = new Chart(occCtx, { type: 'bar', data: occData, options: { scales: { y: { beginAtZero: true, max: 100 } } } })
    const nsCtx = (document.getElementById('noShowChart') as HTMLCanvasElement)?.getContext('2d')!
    const ns = new Chart(nsCtx, { type: 'doughnut', data: noShowData })
    return () => { occ.destroy(); ns.destroy() }
  }, [occData, noShowData])
  return null
}



